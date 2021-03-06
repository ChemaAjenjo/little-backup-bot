package com.littlebackup.web.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.littlebackup.utils.Constants;
import com.littlebackup.web.model.Picture;

@Controller
public class LittleBackupWebController {

	@RequestMapping(value = { "/welcome" }, method = RequestMethod.GET)
	public String getHomePage(ModelMap model) {
		model.addAttribute("name", "mundo");
		return "welcome";
	}

	@RequestMapping(value = { "/file" }, method = RequestMethod.GET)
	public String getFileList(ModelMap model) {
		model.addAttribute("fileList", getFileListing(Constants.HOME_DIR));
		return "files";
	}

	private ArrayList<Picture> getFileListing(String directoryName) {
		File[] files = new File(directoryName).listFiles();

		ArrayList<Picture> filPaths = new ArrayList<Picture>();
		for (File file : files) {
			if (file.isFile()) {
				
				filPaths.add(new Picture(file.getAbsolutePath(), file.getName()));

			} else if (file.isDirectory()) {
				for(Picture file2 : getFileListing(file.getAbsolutePath())){
					filPaths.add(file2);					
				}
			}
		}
		return filPaths;
	}

    @RequestMapping(value="/download", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse response, @RequestParam("id") String id) throws IOException {
     
        File file = null;
         
    		for (Picture picture : getFileListing(Constants.HOME_DIR)) {
    			if (picture.getName().equals(id)) {
    			    file  = new File(picture.getPath());
    			}
    		}
         
        if(!file.exists()){
            String errorMessage = "Sorry. The file you are looking for does not exist";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }
         
        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }
         
        System.out.println("mimetype : "+mimeType);
         
        response.setContentType(mimeType);
         
        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser 
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));
 
         
        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
         
        response.setContentLength((int)file.length());
 
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
 
        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }
 

}
