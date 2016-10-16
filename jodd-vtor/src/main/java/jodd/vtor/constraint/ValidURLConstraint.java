package jodd.vtor.constraint;

import jodd.vtor.ValidationConstraint;
import jodd.vtor.ValidationConstraintContext;

import java.util.regex.Pattern;

public class ValidURLConstraint implements ValidationConstraint<ValidURL> {

	private static final Pattern URL_REGEXP = Pattern.compile(
		"^(?:(?:https?|ftp):\\/\\/)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$"
	);

	// ---------------------------------------------------------------- configure

	public void configure(ValidURL annotation) {
	}

	// ---------------------------------------------------------------- validate

	public boolean isValid(ValidationConstraintContext vcc, Object value) {
		return validate(value);
	}

	public static boolean validate(Object value) {
		if (value == null) {
			return true;
		}

		String url = value.toString().trim();
		return URL_REGEXP.matcher(url).matches();
	}

}