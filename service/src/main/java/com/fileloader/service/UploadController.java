package com.fileloader.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
	
	@Autowired
	FileLineRepository flr;
	
	@Autowired
	private Environment env;
	
	@GetMapping("/")
	public String index() {
		return "<!doctype html><html lang=\"en\"><head><meta charset=\"utf-8\"/><link rel=\"icon\" href=\"/favicon.ico\"/><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/><meta name=\"theme-color\" content=\"#000000\"/><meta name=\"description\" content=\"Web site created using create-react-app\"/><link rel=\"apple-touch-icon\" href=\"/logo192.png\"/><link rel=\"manifest\" href=\"/manifest.json\"/><title>React App</title><link href=\"/static/css/main.8c8b27cf.chunk.css\" rel=\"stylesheet\"></head><body><noscript>You need to enable JavaScript to run this app.</noscript><div id=\"root\"></div><script>!function(e){function r(r){for(var n,a,i=r[0],c=r[1],l=r[2],s=0,p=[];s<i.length;s++)a=i[s],Object.prototype.hasOwnProperty.call(o,a)&&o[a]&&p.push(o[a][0]),o[a]=0;for(n in c)Object.prototype.hasOwnProperty.call(c,n)&&(e[n]=c[n]);for(f&&f(r);p.length;)p.shift()();return u.push.apply(u,l||[]),t()}function t(){for(var e,r=0;r<u.length;r++){for(var t=u[r],n=!0,i=1;i<t.length;i++){var c=t[i];0!==o[c]&&(n=!1)}n&&(u.splice(r--,1),e=a(a.s=t[0]))}return e}var n={},o={1:0},u=[];function a(r){if(n[r])return n[r].exports;var t=n[r]={i:r,l:!1,exports:{}};return e[r].call(t.exports,t,t.exports,a),t.l=!0,t.exports}a.e=function(e){var r=[],t=o[e];if(0!==t)if(t)r.push(t[2]);else{var n=new Promise((function(r,n){t=o[e]=[r,n]}));r.push(t[2]=n);var u,i=document.createElement(\"script\");i.charset=\"utf-8\",i.timeout=120,a.nc&&i.setAttribute(\"nonce\",a.nc),i.src=function(e){return a.p+\"static/js/\"+({}[e]||e)+\".\"+{3:\"a4e240ab\"}[e]+\".chunk.js\"}(e);var c=new Error;u=function(r){i.onerror=i.onload=null,clearTimeout(l);var t=o[e];if(0!==t){if(t){var n=r&&(\"load\"===r.type?\"missing\":r.type),u=r&&r.target&&r.target.src;c.message=\"Loading chunk \"+e+\" failed.\\n(\"+n+\": \"+u+\")\",c.name=\"ChunkLoadError\",c.type=n,c.request=u,t[1](c)}o[e]=void 0}};var l=setTimeout((function(){u({type:\"timeout\",target:i})}),12e4);i.onerror=i.onload=u,document.head.appendChild(i)}return Promise.all(r)},a.m=e,a.c=n,a.d=function(e,r,t){a.o(e,r)||Object.defineProperty(e,r,{enumerable:!0,get:t})},a.r=function(e){\"undefined\"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:\"Module\"}),Object.defineProperty(e,\"__esModule\",{value:!0})},a.t=function(e,r){if(1&r&&(e=a(e)),8&r)return e;if(4&r&&\"object\"==typeof e&&e&&e.__esModule)return e;var t=Object.create(null);if(a.r(t),Object.defineProperty(t,\"default\",{enumerable:!0,value:e}),2&r&&\"string\"!=typeof e)for(var n in e)a.d(t,n,function(r){return e[r]}.bind(null,n));return t},a.n=function(e){var r=e&&e.__esModule?function(){return e.default}:function(){return e};return a.d(r,\"a\",r),r},a.o=function(e,r){return Object.prototype.hasOwnProperty.call(e,r)},a.p=\"/\",a.oe=function(e){throw console.error(e),e};var i=this[\"webpackJsonpfile-reader\"]=this[\"webpackJsonpfile-reader\"]||[],c=i.push.bind(i);i.push=r,i=i.slice();for(var l=0;l<i.length;l++)r(i[l]);var f=c;t()}([])</script><script src=\"/static/js/2.b9024fab.chunk.js\"></script><script src=\"/static/js/main.61d78eda.chunk.js\"></script></body></html>";
	}
	@RequestMapping(value="/uploadfile", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload( 
            @RequestParam("file") MultipartFile file){
            String fileName = "myfile.txt";
            int batchMaxSize = Integer.parseInt(env.getProperty("fileLoader.batchMaxSize"));
            long start = System.currentTimeMillis();
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream = 
                        new BufferedOutputStream(new FileOutputStream(new File(fileName + "-uploaded")));
                stream.write(bytes);
                stream.close();
                List<FileLineModel> listFromFileWithLimit = new ArrayList<>();
               
                try (FileReader reader = new FileReader(fileName + "-uploaded");
                       BufferedReader br = new BufferedReader(reader)) {
                       String line = br.readLine();
                       flr.save(new FileLineModel(line));
                       while (line != null) {
                    	   for(int i = 0; i < batchMaxSize; i++) {
                    		   line = br.readLine();
                    		   if(line == null) break;
                    		   listFromFileWithLimit.add(new FileLineModel(line));
                    	   }
                    	   flr.saveAll(listFromFileWithLimit);
                    	   //System.out.println(listFromFileWithLimit.size());
                    	   listFromFileWithLimit.clear();
                    	   //System.out.println(listFromFileWithLimit.size());
                       }
                   } catch (IOException e) {
                	   return e.getMessage();
                   }
                
                long finish = System.currentTimeMillis();
                long elapsed = finish - start;
                System.out.println("Прошло времени, с: " +  (int)((elapsed / 1000) % 60));
                return "You successfully uploaded " + fileName + " into " + fileName + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + fileName + " => " + e.getMessage();
            }
            
        } else {
            return "You failed to upload " + fileName + " because the file was empty.";
        }
        
       
    }

}
