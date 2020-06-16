package com.example.demo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Set;

@RestController
public class Web {
    HashMap<String, BufferedImage> map = new HashMap<>();
    int id=1;
    @ResponseBody
    @RequestMapping(value = "/image/add",method = RequestMethod.POST)
    public Set addImage(HttpServletRequest requestEntity) throws Exception {
        try {
            BufferedImage image=ImageIO.read(requestEntity.getInputStream());
            map.put(String.valueOf(id),image);
            id++;
        }
        catch (Exception e){
            throw new ResourceNotFound("Image Not Found");

        }
        return map.keySet();
    }

    @RequestMapping(value = "/image/del",method = RequestMethod.DELETE)
    public Set<String> getGrayImage(@RequestParam(value = "id") String id) throws Exception {
        try {
            map.remove(id);
        }
        catch (Exception e){
            throw new ResourceNotFound("Image Not Found");
        }
        return map.keySet();
    }

    @RequestMapping(value = "/image/size",method = RequestMethod.GET)
    public HashMap getSize(@RequestParam(value = "id") String id) throws Exception {
        int a;
        int b;
    try {
        a = map.get(id).getWidth();
        b = map.get(id).getHeight();
    }
    catch (Exception e) {
        throw new ResourceNotFound("Image Not Found");
    }

    HashMap<String, Integer> s = new HashMap<>();
    s.put("heigth", b);
    s.put("width", a);

        return s;

    }

    @RequestMapping(value = "/image/histogram",method = RequestMethod.GET)
    public HashMap gethistogram(@RequestParam(value = "id") String id) throws Exception {
        int a;
        int b;
        try {
            a = map.get(id).getWidth();
            b = map.get(id).getHeight();
        }
        catch (Exception e){
            throw new ResourceNotFound("Image Not Found");
        }

        int tab[][][] = new int[256][256][256];

        for (int i = 0; i < a; i++) {
            for (int j = 0; j < b; j++) {
                Color c = new Color(map.get(id).getRGB(i, j));
                tab[c.getRed()][c.getGreen()][c.getBlue()]++;

            }
        }

        HashMap<String,Integer>s=new HashMap<>();
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    if (tab[i][j][k] > 0) {
                        s.put("Red["+i+"]"+"  Green["+j+"]"+"  Blue["+k+"]",tab[i][j][k]);
                    }


                }
            }
        }
        return s;
    }



    @RequestMapping(value = "/image/crop", method = RequestMethod.GET, produces =
            MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]>  getImage(@RequestParam("id") String id, @RequestParam("x") String x,
                                            @RequestParam("y") String y, @RequestParam("w") String w,
                                            @RequestParam("h") String h) throws Exception {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(map.get(id)
                            .getSubimage(Integer.parseInt(x),
                                    Integer.parseInt(y),
                                    Integer.parseInt(w),
                                    Integer.parseInt(h)),
                    "png", baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG) .body(imageInByte);
        }
        catch (Exception e){
            throw new ResourceNotFound("Image Not Found");
        }
    }

}