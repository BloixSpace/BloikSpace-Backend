package ink.wyy.controller;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@WebServlet(value = "/file/*", loadOnStartup = 1)
@MultipartConfig(
        maxFileSize = 200 * 1024 * 1024,
        maxRequestSize = 200 * 1024 * 1024
)
public class FileController extends HttpServlet {

    Gson gson;

    @Override
    public void init() throws ServletException {
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        switch (uri) {
            case "/file/upload":
                doUpload(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void doUpload(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part part = req.getPart("file");
        if (part.getSize() >= 5 * 1024 * 1024) {
            HashMap<String, Object> res = new HashMap<>();
            res.put("status", 0);
            res.put("errMsg", "文件过大");
            resp.getWriter().write(gson.toJson(res));
            return;
        }

        String disposition = part.getSubmittedFileName();
        String suffix = disposition.substring(disposition.lastIndexOf("."));

        if (suffix.equals("") || suffix.equals(".exe") || suffix.equals(".sh")) {
            HashMap<String, Object> res = new HashMap<>();
            res.put("status", 0);
            res.put("errMsg", "文件类型不允许");
            resp.getWriter().write(gson.toJson(res));
            return;
        }

        String folder = "files";
        if (suffix.equals(".jpg") || suffix.equals(".png")) {
            folder = "img";
        }
        String filename = UUID.randomUUID() + suffix;
        String serverpath = req.getServletContext().getRealPath(folder);

        File fileDisk = new File(serverpath);
        if (!fileDisk.exists()){
            fileDisk.mkdir();
        }
        String fileparts = serverpath + "/" + filename;
        System.out.println(fileparts);
        part.write(fileparts);

        String uri = "/" + folder + "/" + filename;

        HashMap<String, Object> res = new HashMap<>();
        res.put("status", 1);
        res.put("uri", uri);
        resp.getWriter().write(gson.toJson(res));
    }
}
