package com.robertbalazsi.techcompass.twofactorauth.servlet;

import com.robertbalazsi.techcompass.twofactorauth.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

/**
 * Serves file requests. It uses an external uploads directory, configured as 'files.dir' in application.properties.
 */
@WebServlet("/files/*")
public class FileServlet extends HttpServlet {

    @Autowired
    private ApplicationProperties appProps;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filename = URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8");
        File file = new File(appProps.getFilesDir(), filename);
        resp.setHeader("Content-Type", getServletContext().getMimeType(filename));
        resp.setHeader("Content-Length", String.valueOf(file.length()));
        resp.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
