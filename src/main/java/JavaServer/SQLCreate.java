package JavaServer;

public class SQLCreate {
	final public String Select(String column,String from, String where) {
		return "SELECT "+column+" FROM "+from+" WHERE "+where;
	}
	final public String Conditional(String logicalop ,String str1,String perator,String str2) {
		return logicalop+" "+str1+" "+perator+" "+str2;
	}
	final public String between(String logicalop ,String str1,String str2) {
		return logicalop+" between "+str1+" and "+str2;
	}
	final public String like(String logicalop ,String str1,String str2) {
		return logicalop+" "+str1+" like '"+str2+"'";
	}
	final public String in(String logicalop,String feld,String[] values) {
		String inValue=null;
		for (int i = 0; i < values.length; i++) {
			inValue+=" , "+values[i];
		}
		return logicalop+feld+" in("+inValue+")";
	}
	final public String where(String[] Conditionals) {
		String ConditionalName=null;
		for (int i = 0; i < Conditionals.length; i++) {
			ConditionalName+=Conditionals[i];
		}
		return ConditionalName;
		
	}
	final public String from(String[] froms) {
		String fromName=null;
		for (int i = 0; i < froms.length; i++) {
			fromName+=froms[i];
		}
		return fromName;
	}
	final public String column(String[] columns) {
		String columnName=null;
		for (int i = 0; i < columns.length; i++) {
			columnName+=columns[i];
		}
		return columnName;
	}
}
