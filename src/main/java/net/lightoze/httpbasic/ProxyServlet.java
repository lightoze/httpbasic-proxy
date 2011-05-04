package net.lightoze.httpbasic;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import org.apache.wicket.util.crypt.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyFactory;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vladimir Kulev
 */
public class ProxyServlet extends HttpServlet {
    private final Pattern pattern = Pattern.compile(".*/(\\d+)/([\\w\\-+/=]+)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Matcher matcher = pattern.matcher(req.getRequestURI());
        if (!matcher.matches()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        UserKey key;
        EntityManager em = Application.emf.createEntityManager();
        try {
            key = em.find(UserKey.class, Long.parseLong(matcher.group(1)));
        } finally {
            em.close();
        }
        if (key == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, KeyFactory.getInstance("RSA").generatePrivate(new RSAPrivateKeySpec(
                    new BigInteger(key.getPublicKey(), 16),
                    new BigInteger(key.getPrivateKey(), 16)
            )));
            String decryptedParams = "";
            for (String str : Splitter.on('-').split(matcher.group(2))) {
                ByteArrayInputStream encryptedParams = new ByteArrayInputStream(Base64.decodeBase64(str.getBytes(Charsets.US_ASCII)));
                decryptedParams += new String(ByteStreams.toByteArray(new CipherInputStream(encryptedParams, cipher)), Charsets.UTF_8);
            }
            Iterator<String> split = Splitter.on('|').limit(3).split(decryptedParams).iterator();
            URLConnection connection = new URL(split.next()).openConnection();
            connection.setUseCaches(false);
            String credentials = new String(Base64.encodeBase64((split.next() + ":" + split.next()).getBytes(Charsets.UTF_8)), Charsets.US_ASCII);
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            InputStream stream = connection.getInputStream();
            ByteStreams.copy(stream, resp.getOutputStream());
            stream.close();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
