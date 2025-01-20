package fise2.image4.projetinfo;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.opencv.core.Mat;
import org.opencv.core.CvType;

public class OpenCVUtils {

    public static WritableImage matToImage(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        WritableImage image = new WritableImage(width, height);
        PixelWriter pixelWriter = image.getPixelWriter();

        if (mat.type() == CvType.CV_8UC3) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    double[] rgb = mat.get(y, x);
                    Color color = Color.rgb((int) rgb[2], (int) rgb[1], (int) rgb[0]);
                    pixelWriter.setColor(x, y, color);
                }
            }
        }
        return image;
    }
}
