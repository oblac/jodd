// Copyright (c) 2003-2011, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.sqlgen;

import jodd.util.StringUtil;
import jodd.util.StringPool;
import static jodd.util.CharUtil.*;

/**
 * Internal template parser.
 */
class TemplateParser {

	// ---------------------------------------------------------------- parsing

	protected static final char ESCAPE_CHARACTER = '\\';	
	protected static final String MACRO_TABLE = "$T{";
	protected static final String MACRO_COLUMN = "$C{";
	protected static final String MACRO_MATCH = "$M{";
	protected static final String MACRO_VALUE = "$V{";

	/**
	 * Parses template and returns generated sql builder.
	 */
	public void parse(DbSqlBuilder sqlBuilder, String template) {
		int length = template.length();
		int last = 0;
		while (true) {
			int mark = template.indexOf('$', last);
			if (mark == -1) {
				if (last < length) {
					sqlBuilder.appendRaw(template.substring(last));
				}
				break;
			}

			int escapesCount = countEscapes(template, mark);                // check if escaped
			if (escapesCount > 0) {
				boolean isEscaped = escapesCount % 2 != 0;
				int escapesToAdd = escapesCount >> 1;
				sqlBuilder.appendRaw(template.substring(last, mark - escapesCount + escapesToAdd) + '$');
				if (isEscaped) {
					last = mark + 1;
					continue;
				}
			} else {
				sqlBuilder.appendRaw(template.substring(last, mark));
			}

			int end;

			if (template.startsWith(MACRO_TABLE, mark)) {
				mark += MACRO_TABLE.length();
				end = findMacroEnd(template, mark);
				onTable(sqlBuilder, template.substring(mark, end));
			} else if (template.startsWith(MACRO_COLUMN, mark)) {
				mark += MACRO_COLUMN.length();
				end = findMacroEnd(template, mark);
				onColumn(sqlBuilder, template.substring(mark, end));
			} else if (template.startsWith(MACRO_MATCH, mark)) {
				mark += MACRO_MATCH.length();
				end = findMacroEnd(template, mark);
				onMatch(sqlBuilder, template.substring(mark, end));
			} else if (template.startsWith(MACRO_VALUE, mark)) {
				mark += MACRO_VALUE.length();
				end = findMacroEnd(template, mark);
				onValue(sqlBuilder, template.substring(mark, end));
			} else {
				mark++;           // reference found
				end = mark;       // find macro end
				while (end < length) {
					if (isReferenceChar(template, end) == false) {
						break;
					}
					end++;
				}
				onReference(sqlBuilder, template.substring(mark, end));
				end--;
			}
			end++;
			last = end;
		}
	}

	protected static boolean isReferenceChar(String template, int index) {
		char c = template.charAt(index);
		if ((c == '+') && (template.charAt(index - 1) == '.')) {
			return true;
		}
		return isDigit(c) || isLetter(c) || (c == '_') || (c == '.');
	}



	/**
	 * Finds macros end.
	 */
	protected int findMacroEnd(String template, int fromIndex) {
		int endIndex = template.indexOf('}', fromIndex);
		if (endIndex == -1) {
			throw new DbSqlBuilderException("Template not formed properly, some macros are not closed. Error at: '..." + template.substring(fromIndex));
		}
		return endIndex;
	}

	/**
	 * Count escapes to the left.
	 */
	protected int countEscapes(String template, int macroIndex) {
		macroIndex--;
		int escapeCount = 0;
		while (macroIndex >= 0) {
			if (template.charAt(macroIndex) != ESCAPE_CHARACTER) {
				break;
			}
			escapeCount++;
			macroIndex--;
		}
		return escapeCount;
	}

	// ---------------------------------------------------------------- handlers

	protected void onTable(DbSqlBuilder sqlBuilder, String allTables) {
		String[] tables = StringUtil.split(allTables, StringPool.COMMA);
		for (String table : tables) {
			sqlBuilder.table(table);
		}
	}

	protected void onColumn(DbSqlBuilder sqlBuilder, String allColumns) {
		String[] columns = StringUtil.split(allColumns, StringPool.COMMA);
		for (String column : columns) {
			sqlBuilder.column(column);
		}
	}

	protected void onReference(DbSqlBuilder sqlBuilder, String reference) {
		sqlBuilder.ref(reference);
	}

	protected void onMatch(DbSqlBuilder sqlBuilder, String expression) {
		sqlBuilder.match(expression);
	}

	protected void onValue(DbSqlBuilder sqlBuilder, String expression) {
		sqlBuilder.colvalue(expression);
	}


}
