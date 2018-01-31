package JavaServer;

public class SQLCreate {
	static class Delete {
		final public String getDelete(String table) {
			return "DELETE FROM " + table;
		}

		final public String getDelete(String table, String where) {
			return "DELETE FROM " + table + " WHERE " + where;
		}
	}

	static class Insert {

		final public String getInsert(String table, Value value) {
			return "INSERT INTO " + table + " (" + value.field + " VALUES" + value.value;
		}

		final public String getInsert(String table, String value) {
			return "INSERT INTO " + table + " VALUES(" + value + ")";
		}

		final public String getInsert(String table, Value[] values) {
			String field = values[0].field;
			String value = values[0].value;
			for (int i = 1; i < values.length; i++) {
				field += "," + values[i].field;
				value += "," + values[i].value;
			}
			return "INSERT INTO " + table + " (" + field + ") VALUES (" + value + ")";
		}

		final public String value(String[] values) {
			String value = values[0];
			for (int i = 1; i < values.length; i++) {
				value += "," + values[i];
			}
			return value;
		}

		static class Value {
			String field;
			String value;

			public Value(String field, String value) {
				this.field = field;
				this.value = value;
			}
		}
	}

	static class Select {
		final public String column(String[] columns) {
			String columnName = columns[0];
			for (int i = 1; i < columns.length; i++) {
				columnName += ", " + columns[i];
			}
			return columnName;
		}

		final public String from(String[] froms) {
			String fromName = froms[0];
			for (int i = 1; i < froms.length; i++) {
				fromName += ", " + froms[i];
			}
			return fromName;
		}

		final public String getSelect(String from) {
			return getSelect("*", from);
		}

		final public String getSelect(String field, String from) {
			return "SELECT " + field + " FROM " + from;
		}

		final public String getSelect(String field, String from, String where) {
			return getSelect(field, from + " WHERE " + where);
		}

		final public String where(String[] Conditionals) {
			String ConditionalName = null;
			for (int i = 0; i < Conditionals.length; i++) {
				ConditionalName += Conditionals[i] + " ";
			}
			return ConditionalName;
		}
	}

	static class Update {
		final public String getUpdate(String table, String field, String value) {
			return "UPDATE " + table + " SET " + field + "=" + value;
		}

		final public String getUpdate(String table, String field, String value, String where) {
			return "UPDATE " + table + " SET " + field + "=" + value + " WHERE " + where;
		}

		final public String where(String Conditionals) {
			return Conditionals;
		}

		final public String where(String[] Conditionals) {
			String ConditionalName = null;
			for (int i = 0; i < Conditionals.length; i++) {
				ConditionalName += Conditionals[i] + " ";
			}
			return ConditionalName;

		}
	}

	static class Where {
		final public String between(String logicalop, String str1, String str2) {
			return logicalop + " between " + str1 + " and " + str2;
		}

		final public String Conditional(String logicalop, String field, String perator, String condition) {
			/*
			 * logicalop =and나 or같은 논리 연산자 field =조건을 검사할 필드 perator =조건 연산자 condition =값
			 */
			return logicalop + " " + field + " " + perator + " " + condition;
		}

		final public String in(String logicalop, String feld, String[] values) {
			String inValue = values[0];
			for (int i = 1; i < values.length; i++) {
				inValue += " , " + values[i];
			}
			return logicalop + feld + " in(" + inValue + ")";
		}

		final public String like(String logicalop, String str1, String str2) {
			return logicalop + " " + str1 + " like '" + str2 + "'";
		}

		final public String limit(int limit) {
			return "LIMIT " + limit;
		}

		final public String limit(int startnum, int limit) {
			return "LIMIT " + startnum + ", " + limit;
		}

		final public String sort(Sort sort) {
			return "ORDER BY " + sort.toString();
		}

		final public String sort(Sort[] sorts) {
			String sortstr = "ORDER BY " + sorts[0];
			for (int i = 1; i < sorts.length; i++) {
				sortstr += ", " + sorts[i].toString();
			}
			return sortstr;
		}

		static class Sort {
			String field;
			boolean sort;

			public Sort(String field, boolean sort) {
				// true는 오름차순, false는 내림차순
				this.field = field;
				this.sort = sort;
			}

			@Override
			public String toString() {
				return field + " " + ((sort) ? "ASC" : "DESC");
			}
		}
	}

	static class Create {
		final String getCreate(String table, String field, String primary) {
			return "CREATE TABLE " + table + "(" + field + "," + primary + ")DEFAULT CHARSET=utf8";
		}

		final String fieldChange(Object obj) {
			if (obj instanceof SQLField) {
				SQLField field = (SQLField) obj;
				return field.toString();
			}
			return null;
		}

		final String fieldChange(Object[] obj) {
			if (obj instanceof SQLField[]) {
				SQLField[] fields = (SQLField[]) obj;
				String field = fields[0].toString();
				for (int i = 1; i < fields.length; i++) {
					field += ", " + fields[i].toString();
				}
				return field;
			}
			return null;
		}

		final String primary(String[] primarys) {
			String primary = "PRIMARY KEY (" + primarys[0] + ")";
			for (int i = 1; i < primarys.length; i++) {
				primary += ", PRIMARY KEY (" + primarys[i] + ")";
			}
			return primary;
		}

		final String primary(String primary) {
			return "PRIMARY KEY (" + primary + ")";
		}

		static class SQLField {
			String fieldName;
			String fieldType;
			String etcSetting;
			int fieldSize = -1;

			public SQLField(String fieldName, String fieldType) {
				this.fieldName = fieldName;
				this.fieldType = fieldType;
			}

			public SQLField(String fieldName, String fieldType, int fieldSize) {
				this(fieldName, fieldType);
				this.fieldSize = fieldSize;
			}

			public SQLField(String fieldName, String fieldType, String etcSetting) {
				this(fieldName, fieldType);
				this.etcSetting = etcSetting;
			}

			public SQLField(String fieldName, String fieldType, int fieldSize, String etcSetting) {
				this(fieldName, fieldType, fieldSize);
				this.etcSetting = etcSetting;
			}

			@Override
			public String toString() {
				String fieldsize = (this.fieldSize == -1) ? "" : "(" + this.fieldSize + ")";
				return fieldName + " " + fieldType + fieldsize + etcSetting;
			}
		}
	}

	public Insert insert;

	public Select select;

	public Update update;

	public Delete delete;

	public Where where;

	public Create create;

	public SQLCreate() {
		create=new Create();
		insert = new Insert();
		select = new Select();
		update = new Update();
		delete = new Delete();
		where = new Where();
	}
}
