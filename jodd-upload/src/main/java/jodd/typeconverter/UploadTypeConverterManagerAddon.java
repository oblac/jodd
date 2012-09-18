package jodd.typeconverter;

import jodd.typeconverter.impl.FileConverter;
import jodd.typeconverter.impl.FileUploadConverter;
import jodd.typeconverter.impl.FileUploadToFileTypeConverter;
import jodd.upload.FileUpload;

import java.io.File;

public class UploadTypeConverterManagerAddon {

	public static void registerDefaults(TypeConverterManagerBean typeConverterManagerBean) {
		typeConverterManagerBean.register(FileUpload.class, new FileUploadConverter());

		FileConverter fileConverter = (FileConverter) typeConverterManagerBean.lookup(File.class);

		fileConverter.registerAddonConverter(new FileUploadToFileTypeConverter());
	}
}
