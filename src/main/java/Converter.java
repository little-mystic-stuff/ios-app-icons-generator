import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Converter {
    public static void createImage(String inputFilePath, String filePath, int width, int height) throws Exception {
        String svg_URI_input = new File(inputFilePath).toURI().toString();
        TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
        OutputStream png_ostream = new FileOutputStream(filePath);
        TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
        PNGTranscoder my_converter = new PNGTranscoder();
        my_converter.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 1f * width);
        my_converter.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 1f * height);
        my_converter.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE, Boolean.TRUE);
        my_converter.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);
        my_converter.transcode(input_svg_image, output_png_image);
        png_ostream.flush();
        png_ostream.close();
    }

    public static void removeAlphaChannelFromPng(String filePath) throws IOException {
        BufferedImage img = ImageIO.read(new URL("file://" + filePath));
        BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = copy.createGraphics();
        g2d.fillRect(0, 0, copy.getWidth(), copy.getHeight());
        g2d.drawImage(img, 0, 0, null);
        File outputFile = new File(filePath);
        ImageIO.write(copy, "png", outputFile);
        g2d.dispose();
    }

    public static void main (String[] args){
        Map<String, Integer> icons = new HashMap<>();
        icons.put("app_store", 1024);
        icons.put("app_icon_iphone_60pt_2x", 120);
        icons.put("app_icon_iphone_60pt_3x", 180);
        icons.put("app_icon_ipad_76pt", 76);
        icons.put("app_icon_ipad_2x_76pt", 152);
        icons.put("app_icon_ipadpro_3.5pt_2x", 167);
        icons.put("notification_iphone_20pt_2x", 40);
        icons.put("notification_iphone_20pt_3x", 60);
        icons.put("notification_ipad_20pt", 20);
        icons.put("notification_ipad_20_2xpt", 40);
        icons.put("spotlight_iphone_40pt_2x", 80);
        icons.put("spotlight_iphone_40pt_3x", 120);
        icons.put("spotlight_ipad_40pt", 40);
        icons.put("spotlight_ipad_40pt_2x", 48);
        icons.put("settings_iphone_29pt_2x", 58);
        icons.put("settings_iphone_29pt_3x", 87);
        icons.put("settings_ipad_29pt", 29);
        icons.put("settings_ipad_29pt_2x", 58);

        String inputSvgPath = null;
        String outputDirectory = null;
        if (args.length < 2) {
            System.out.println("""
                    Invalid arguments.
                    Usage: java -jar icon-generator.jar PATH_TO_SOURCE_FILE PATH_TO_OUTPUT_DIRECTORY
                    """);
            System.exit(1);
        } else {
            inputSvgPath = args[0];
            outputDirectory = args[1];
        }

        for(String iconName : icons.keySet()) {
            String filename = iconName + ".png";
            String filePath = outputDirectory + filename;
            try {
                createImage(inputSvgPath, filePath, icons.get(iconName), icons.get(iconName));
                System.out.println("File " + filename + " has been created.");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            try {
                removeAlphaChannelFromPng(filePath);
                System.out.println("Alpha channel in file " + filename + " has been removed.");
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
