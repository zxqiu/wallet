package QRCode;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.zxing.BarcodeFormat;  
import com.google.zxing.EncodeHintType;  
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.wallet.service.WalletService;

public class QRCodeUtils {
	private static final Logger logger_ = LoggerFactory.getLogger(WalletService.class);
	
	public QRCodeUtils() {
	}
	
	public static int QRCodeGenPNG(String text, String filename) throws IOException {
		return QRCodeGen(text, filename, "png");
	}
	
	public static int QRCodeGenPNG(String text, String filename, int width, int height) throws IOException {
		return QRCodeGen(text, filename, "png", width, height);
	}
	
	public static int QRCodeGen(String text, String filename, String format) throws IOException {
		int width = 300;
        int height = 300;
		
        return QRCodeGen(text, filename, format, width, height);
	}
	
	public static int QRCodeGen(String text, String filename, String format, int width, int height) throws IOException {
		
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   // 内容所使用字符集编码
        
        BitMatrix bitMatrix;
		try {
			bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
			
			// 生成二维码  
	        File outputFile = new File(filename);  
	        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
	        logger_.warn("QR code file " + filename + " generated successfully");
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger_.error("cannot encode QR code text");
		}
        
		return 0;
	}
	
	// test
	public static void main(String[] args) throws Exception {
	    logger_.warn("test QR Code generator");
	    QRCodeUtils.QRCodeGenPNG("http://www.google.com", "src/main/resources/assets/QRCodes" + File.separator + "test.png");
		Path path = FileSystems.getDefault().getPath("", "src/main/resources/assets/QRCodes" + File.separator + "test.png");
		//try {
			//Files.deleteIfExists(path);
		//} catch (IOException e) {
		//	e.printStackTrace();
		//	logger_.error("ERROR: failed to delete QR Code :" + "src/main/resources/assets/QRCodes" + File.separator + "test.png");
		//}
	}
} 