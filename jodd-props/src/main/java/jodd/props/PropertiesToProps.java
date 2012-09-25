package jodd.props;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

/**
 * Converter for Java Properties to Jodd Props format.
 */
class PropertiesToProps {

	PropertiesToProps() {
	}

	/**
	 * Convert Java Properties to Jodd Props format
	 *
	 * @param writer     Writer to write Props formatted file content to
	 * @param properties Properties to convert to Props format
	 * @param profiles   Properties per profile to convert and add to the Props format
	 * @throws IOException On any I/O error when writing to the writer
	 */
	void convertToWriter(final Writer writer, final Properties properties, final Map<String, Properties> profiles)
			throws IOException {
		final BufferedWriter bw = getBufferedWriter(writer);
		writeBaseAndProfileProperties(bw, properties, profiles);
		writeProfilePropertiesThatAreNotInTheBase(bw, properties, profiles);
		bw.flush();
	}

	private void writeProfilePropertiesThatAreNotInTheBase(final BufferedWriter bw, final Properties baseProperties,
														   final Map<String, Properties> profiles) throws IOException {
		for (final Map.Entry<String, Properties> entry : profiles.entrySet()) {
			final String profileName = entry.getKey();
			final Properties profileProperties = entry.getValue();
			for (final String key : profileProperties.stringPropertyNames()) {
				if (baseProperties.containsKey(key)) {
					continue;
				}
				writeProfileProperty(bw, profileName, key, profileProperties.getProperty(key));
			}
		}
	}

	private BufferedWriter getBufferedWriter(final Writer writer) {
		final BufferedWriter bw;
		if (writer instanceof BufferedWriter) {
			bw = (BufferedWriter) writer;
		} else {
			bw = new BufferedWriter(writer);
		}
		return bw;
	}

	private void writeBaseAndProfileProperties(final BufferedWriter bw, final Properties baseProperties,
											   final Map<String, Properties> profiles) throws IOException {
		for (final String key : baseProperties.stringPropertyNames()) {
			final String value = baseProperties.getProperty(key);
			writeBaseProperty(bw, key, value);
			writeProfilePropertiesOfKey(bw, key, profiles);
		}
	}

	private void writeProfilePropertiesOfKey(final BufferedWriter bw, final String key,
											 final Map<String, Properties> profiles) throws IOException {
		for (final Map.Entry<String, Properties> entry : profiles.entrySet()) {
			final Properties profileProperties = entry.getValue();
			if (!profileProperties.containsKey(key)) {
				continue;
			}
			final String profileName = entry.getKey();
			writeProfileProperty(bw, profileName, key, profileProperties.getProperty(key));
		}
	}

	private void writeProfileProperty(final BufferedWriter bw, final String profileName,
									  final String key, final String value)
			throws IOException {
		bw.write(key + "<" + profileName + ">" + "=" + value);
		bw.newLine();
	}

	private void writeBaseProperty(final BufferedWriter bw, final String key, final String value)
			throws IOException {
		bw.write(key + "=" + value);
		bw.newLine();
	}
}