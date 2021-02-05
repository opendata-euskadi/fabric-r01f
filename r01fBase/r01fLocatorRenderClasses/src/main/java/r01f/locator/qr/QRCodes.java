package r01f.locator.qr;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import r01f.types.StringBase64;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public abstract class QRCodes {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static final Integer defaultTicketMobileQRWidth = 150;
	private static final Map<EncodeHintType,Object> _hintMap = new HashMap<>();
	static {
		_hintMap.put(EncodeHintType.MARGIN,new Integer(0));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * Get a text as QR. Margin is automatically generated to fill the default width.
	 * @param qrData
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64(final String qrData) throws IOException {
		return getQRTicketAsBase64(qrData,
								   defaultTicketMobileQRWidth);
	}
	/**
	 * Get a text as QR. Margin is automatically generated to fill the width.
	 * @param qrData
	 * @param width
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64(final String qrData,
												   final Integer ticketWidth) throws IOException {
		return StringBase64.encode(getQRTicketAsBytes(qrData, 
													  ticketWidth));
	}
	
	/**
	 * Get a text as QR. The QR is generated to get the minimun automatic margin and then scaled to the default width.  
	 * @param qrData
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64Scaled(final String qrData) throws IOException {
		return getQRTicketAsBase64Scaled(qrData, defaultTicketMobileQRWidth);
	}
	public static StringBase64 getQRTicketAsBase64Scaled(final String qrData,
														 final Color color) throws IOException {
		return getQRTicketAsBase64Scaled(qrData, defaultTicketMobileQRWidth, color);
	}
	/**
	 * Get a text as QR. The QR is generated to get the minimun automatic margin and then scaled to the width.  
	 * @param qrData
	 * @param width
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64Scaled(final String qrData,
														 final Integer width) throws IOException {
		return getQRTicketAsBase64Scaled(qrData, 
										 width,Color.BLACK);
	}
	public static StringBase64 getQRTicketAsBase64Scaled(final String qrData,
														 final Integer width,
														 final Color color) throws IOException {
		StringBase64 base64bytes = null;
		byte[] noScaledBytes = getQRTicketAsBytesWithMinSizeOver(qrData, 
																 width,color);
		byte[] scaledBytes = _scale(noScaledBytes, width, width, color);
		base64bytes = StringBase64.encode(scaledBytes);
		return base64bytes;
	}
	public static BufferedImage getQRTicketAsBufferedImageScaled(final String qrData) throws IOException {
		return getQRTicketAsBufferedImageScaled(qrData,
												defaultTicketMobileQRWidth);
	}
	public static BufferedImage getQRTicketAsBufferedImageScaled(final String qrData,
																 final Color color) throws IOException {
		return getQRTicketAsBufferedImageScaled(qrData, 
												defaultTicketMobileQRWidth,color);
	}
	public static BufferedImage getQRTicketAsBufferedImageScaled(final String qrData,
																 final Integer width) throws IOException {
		return getQRTicketAsBufferedImageScaled(qrData,
												width,Color.BLACK);
	}
	public static BufferedImage getQRTicketAsBufferedImageScaled(final String qrData,
																 final Integer width,final Color color) throws IOException {
		byte[] noScaledBytes = getQRTicketAsBytesWithMinSizeOver(qrData, width);
		BufferedImage buffImage = _scaleAsBufferedImage(noScaledBytes, width, width, color);
		return buffImage;
	}
	/**
	 * Get a text as QR. The QR is generated to get the minimun automatic margin with the max size under the default width.
	 * @param qrData
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64WithMaxSizeUnder(final String qrData) throws IOException {
		return getQRTicketAsBase64WithMaxSizeUnder(qrData, 
												   defaultTicketMobileQRWidth);
	}
	/**
	 * Get a text as QR. The QR is generated to get the minimun automatic margin with the max size under the width.
	 * @param qrData
	 * @param width
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64WithMaxSizeUnder(final String qrData,
														 		   final Integer width) throws IOException {
		try {
			QRCode qrCode = Encoder.encode(qrData, ErrorCorrectionLevel.M);
			int multiplicator = (int) Math.floor(new Float(width)/new Float(qrCode.getMatrix().getWidth()));
			int qrSize = qrCode.getMatrix().getWidth();
			if (multiplicator > 0) {
				qrSize = qrCode.getMatrix().getWidth() * (int) Math.floor(new Float(width)/new Float(qrCode.getMatrix().getWidth()));
			}
			return getQRTicketAsBase64(qrData, 
									   qrSize);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Get a text as QR. The QR is generated to get the minimun automatic margin with the min size over the default width.
	 * @param qrData
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64WithMinSizeOver(final String qrData) throws IOException {
		return getQRTicketAsBase64WithMinSizeOver(qrData, 
												  defaultTicketMobileQRWidth);
	}
	/**
	 * Get a text as QR. The QR is generated to get the minimun automatic margin with the min size over the width.
	 * @param qrData
	 * @param width
	 * @return
	 * @throws IOException
	 */
	public static StringBase64 getQRTicketAsBase64WithMinSizeOver(final String qrData,
																  final Integer width) throws IOException {
		return StringBase64.encode(getQRTicketAsBytesWithMinSizeOver(qrData, 
																	 width));
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static byte[] getQRTicketAsBytes(final String qrData,
											final Integer ticketWidth) throws IOException {
		return getQRTicketAsBytes(qrData, 
								  ticketWidth,Color.BLACK);
	}
	public static byte[] getQRTicketAsBytes(final String qrData,
											final Integer ticketWidth,final Color color) throws IOException {
		byte[] bytes = null;
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
			
			BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, ticketWidth, ticketWidth, _hintMap);
			MatrixToImageConfig matrixConfig = new MatrixToImageConfig(color.getRGB(),
																	   MatrixToImageConfig.WHITE);
			MatrixToImageWriter.writeToStream(bitMatrix,"PNG",pngOutputStream,matrixConfig);
			
			bytes = pngOutputStream.toByteArray();
		} catch (WriterException e1) {
			e1.printStackTrace();
		}
		return bytes;
	}
	public static byte[] getQRTicketAsBytesWithMinSizeOver(final String qrData,
														   final Integer width) throws IOException {
		return getQRTicketAsBytesWithMinSizeOver(qrData, 
												 width,Color.BLACK);
	}
	public static byte[] getQRTicketAsBytesWithMinSizeOver(final String qrData,
														   final Integer width,final Color color) throws IOException { 
		try {
			QRCode qrCode = Encoder.encode(qrData, ErrorCorrectionLevel.M, _hintMap);
			int qrSize = qrCode.getMatrix().getWidth() * ((int) Math.ceil(new Float(width)/new Float(qrCode.getMatrix().getWidth())));
			return getQRTicketAsBytes(qrData, 
									  qrSize,color);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Scale an image encoded as byte array.
	 * @param fileData
	 * @param width
	 * @param height
	 * @return
	 */
	private static byte[] _scale(final byte[] fileData,
								 final int width,final int height,
								 final Color color) {
		try {
			BufferedImage imageBuff = _scaleAsBufferedImage(fileData,
															width,height, 
															color);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	
			ImageIO.write(imageBuff, "jpg", buffer);
	
			return buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileData;
	}
	
	private static BufferedImage _scaleAsBufferedImage(final byte[] fileData,
													   final int width,final int height,
													   final Color color) {
		ByteArrayInputStream in = new ByteArrayInputStream(fileData);
		try {
			BufferedImage img = ImageIO.read(in);
			int imageHeight = height;
			if (height == 0) {
				imageHeight = (width * img.getHeight())/ img.getWidth(); 
			}
			int imageWidth = width;
			if (width == 0) {
				imageWidth = (height * img.getWidth())/ img.getHeight();
			}
			Image scaledImage = img.getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
			BufferedImage imageBuff = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
			imageBuff.getGraphics().drawImage(scaledImage, 0, 0, color, null);
			return imageBuff;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
