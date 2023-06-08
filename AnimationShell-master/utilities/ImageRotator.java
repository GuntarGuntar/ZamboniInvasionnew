import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageRotator {

	public static Image rotate(Image original, int angle) {

	    AffineTransform tx = new AffineTransform();
	    tx.rotate(Math.toRadians(angle - 90), original.getWidth(null) / 2 , original.getHeight(null) / 2);
	    	    
	    AffineTransformOp op = new AffineTransformOp(tx,
	            AffineTransformOp.TYPE_BILINEAR);
	    
	    Image rotated = op.filter((BufferedImage) original, null);

	    
	    BufferedImage cropped = null;
		try {
			cropped = ((BufferedImage) rotated).getSubimage(0, 0, original.getWidth(null), original.getHeight(null));
		} catch (Exception e) {
			cropped = (BufferedImage) rotated;
		}

		return cropped;
	    
	}
	
	public static BufferedImage deepCopy(BufferedImage bi) {
	    ColorModel cm = bi.getColorModel();
	    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
	    WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
	    return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}	
	
}
