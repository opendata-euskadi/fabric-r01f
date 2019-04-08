package r01f.persistence.db.sql;

import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import r01f.util.types.Strings;

/**
 * Utilidades auxiliares para componer sentencias SQL.
 */
public final class DBSQLHelpper {
    /**
     * Crear una sentencia SQL para realizar una Select.
     * @param columns Columnas que intervienen en la select.
     * @param tables Tablas que intervienen en la select.
     * @param params Lista con los parametros de la parte WHERE de la select.
     * @param where WHERE de la select.
     * @param columnsOrder indice de las columnas por las que se ordena.
     * @param desc indica si los resultados se ordenan de forma descendente para cada columna del parámetro "columsOrder".
     * @param distinctRows Indica si hay que incluir el indicador DISTINCT en la select.
	 * @return La sentencia SQL para realizar la consulta en forma de cadena.
     */
    static String composeSelect(final List<String> columns,
    							final List<String> tables,
    							final String where,
    							final int[] columnsOrder,
    							final boolean[] desc,
    							final boolean distinct) {
        StringBuilder sb = new StringBuilder();
        if (!distinct) {
            sb.append("SELECT ");
            sb.append(_listToCommaDelimitedString(columns));
        } else {
            sb.append("SELECT DISTINCT ");
            sb.append(_listToCommaDelimitedString(columns));
        }
        sb.append(" FROM ");
        sb.append(_listToCommaDelimitedString(tables));

        if (!Strings.isNullOrEmpty(where)) {
            sb.append(" WHERE ");
            sb.append(where);
        }

        if (columnsOrder != null && columnsOrder.length > 0) {
            sb.append(" ORDER BY ");
            for (int i = 0; i < columnsOrder.length; i++) {
                sb.append(columnsOrder[i]);
                if (desc[i]) {
                	sb.append(" DESC");
                } else {
                	sb.append(" ASC");
                }
                if (i != columnsOrder.length-1) {
                	sb.append(", ");
                }
            }
        }

        return sb.toString();
    }
    /**
     * Crear una sentencia SQL para realizar una Insert.
     * @param insertData Parejas de elementos NOMBRE_COLUMNA / VALOR.
     * @param table Nombre de la tabla sobre la que se hace la insert.
     * @return La sentencia SQL para realizar la inserción en forma de cadena.
     */
    static String composeInsert(final List<DBData> insertData,
    							final String table) {
    	StringBuilder sbOut = null;

    	if (insertData!=null && !insertData.isEmpty()) {
    		StringBuilder sbCols = new StringBuilder("");
        	StringBuilder sbValues = new StringBuilder("");

	    	int size = insertData.size();
	        for (DBData currDBData : insertData) {
	            if (!Strings.isNullOrEmpty(currDBData.value)) {
	                sbCols.append(currDBData.columnName);
	                // Si se trata de una función SQL, debe ir sin comillas en la sentencia
	                if (currDBData.isNumeric) {
	                    sbValues.append(currDBData.value);
	                } else {
	                    sbValues.append("'");
	                    sbValues.append(_escapeString(currDBData.value));
	                    sbValues.append("'");
	                }
	                if (--size == 0) { //Ultimo elemento
	                    sbCols.append(',');
	                    sbValues.append(',');
	                }
	            }
	        }

	        sbOut = new StringBuilder(26);
	        sbOut.append("INSERT INTO ");
	        sbOut.append(table);
	        sbOut.append(" (");
	        sbOut.append(sbCols.toString());
	        sbOut.append(") VALUES (");
	        sbOut.append(sbValues.toString());
	        sbOut.append(") ");
    	}

        return sbOut != null ? sbOut.toString() : "";
    }
    /**
     * Crear una sentencia SQL para realizar una Update.
     * @param updateData Parejas de elementos NOMBRE_COLUMNA / VALOR.
     * @param table Tabla sobre la que queremos que se ejecute el update.
     * @param where Claúsula WHERE.
     * @return La sentencia SQL para realizar la update en forma de cadena.
     */
    static String composeUpdate(final List<DBData> updateData,
    							final String table,
    							final String where) {
        StringBuilder sb = null;
        if (updateData != null && !updateData.isEmpty()) {
        	sb = new StringBuilder(15);

        	sb.append("UPDATE ");
            sb.append(table);
            sb.append(" SET ");

	        int size = updateData.size();
	        for (DBData currDBData : updateData) {
	            if (!Strings.isNullOrEmpty(currDBData.value)) {
	                if (currDBData.isNumeric) {
	                    sb.append(currDBData.columnName);
	                    sb.append('=');
	                    sb.append(currDBData.value);
	                } else {
	                    sb.append(currDBData.columnName);
	                    sb.append("= '");
	                    sb.append(_escapeString(currDBData.value));
	                    sb.append("'");
	                }
	                if (--size == 0) {
	                	sb.append(",");
	                }
	            }
	        }

	        if (!Strings.isNullOrEmpty(where)) {
	            sb.append(" WHERE ");
	            sb.append(where);
	        }
        }

        return sb!=null ? sb.toString() : "";
    }
    /**
     * Crear una sentencia SQL para realizar un Borrado.
     * @param table Tabla sobre la que queremos que se ejecute el delete.
     * @param where WHERE del delete.
     * @return La sentencia SQL para realizar el borrado en forma de cadena.
     */
    static String composeDelete(final String table,
    							final String where) {
    	StringBuilder sb = null;

    	if (!Strings.isNullOrEmpty(table)) {
    		sb = new StringBuilder(15);
	        sb.append("DELETE FROM ");
	        sb.append(table);

	        if (!Strings.isNullOrEmpty(where)) {
	            sb.append(" WHERE ");
	            sb.append(where);
	        }
    	}

        return sb != null ? sb.toString() : "";
    }


/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PRIVADOS
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Método que escapa un string (escapa las comillas internas).
     * @param str La cadena a escapar.
     * @return La cadena escapada.
     */
    private static String _escapeString(String str) {
        StringBuffer sbEscape = new StringBuffer(str);
        for (int i = 0; i < sbEscape.length(); i++) {
            if (sbEscape.charAt(i) == '\'') {
                sbEscape.replace(i, i, "'");
                i++;
            }
        }
        return sbEscape.toString();
    }
    /**
     * Crea un string con los elementos de una lista de strings separados por una coma.
     * @param list La lista de cadenas.
     * @return Una cadena con los elementos de la lista separados por coma.
     */
    private static String _listToCommaDelimitedString(List<String> list) {
        StringBuilder sb = null;
        if (list != null && !list.isEmpty()) {
	        sb = new StringBuilder("");

	        int size = list.size();
	        for (String data : list) {
	            sb.append(data);
	            if (--size == 0) {
	            	sb.append(",");
	            }
	        }
        }

        return sb != null ? sb.toString() : "";
    }


/////////////////////////////////////////////////////////////////////////////////////////
//  CLASE QUE MODELA LOS DATOS DE UNA COLUMNA
//////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Modela los datos de una columna
     */
    public class DBData {
        String columnName;
        String value;
        boolean isNumeric;

        public DBData(String inColumnName, String inValue, boolean inIsNumeric) {
            this.columnName = inColumnName;
            this.value = inValue;
            this.isNumeric = inIsNumeric;
        }
        public DBData(String inColumnName, String inValue) {
            this(inColumnName,inValue,false);
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  CLASE AUXILIAR PARA LA CONSTRUCCIÓN DE UNA LISTA DE objetos DBData
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Inner class para la creación de una lista de objetos DBData.<br>
     * Simplemente hacer:
     * <pre class="brush:java">
     *      DBDataListCreator listCreator = SQLHelpper.new DBDataListCreator();
     *      listCreator.addDBData(colName,colValue,numeric);
     * </pre>
     */
    @NoArgsConstructor
    public class DBDataListCreator {
        private List<DBData> _dbDataList = new ArrayList<DBData>();

        public DBData addDBData(final String inColumnName, final String inValue, final boolean inIsNumeric) {
            if (inValue == null) return null;
            DBSQLHelpper.DBData dbData = new DBSQLHelpper().new DBData(inColumnName,inValue,inIsNumeric);
            _dbDataList.add(dbData);
            return dbData;
        }
        public DBData addDBData(final String inColumnName, final String inValue) {
            return this.addDBData(inColumnName,inValue,false);
        }
        public List<DBData> getDBDataList() {
            return _dbDataList;
        }
    }
/////////////////////////////////////////////////////////////////////////////////////////
//  METODOS PARA CREAR OBJETOS DBData
/////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Creación de un objeto columna-valor para generar las sentencias SQL.
     * @param columnName Nombre del campo de la tabla de base de datos.
     * @param value Valor del campo.
     * @return Entidad que representa un objeto columna-valor para generar las sentencias SQL.
     */
    public static DBData createDBData(String columnName, String value) {
        DBDataListCreator dbDataListCreator = new DBSQLHelpper().new DBDataListCreator();
        return dbDataListCreator.addDBData(columnName,value);
    }
    /**
     * Creación de un objeto columna-valor para generar las sentencias SQL.
     * @param columnName Nombre del campo de la tabla de base de datos.
     * @param value Valor del campo.
     * @param isNumeric Indica si se trata de un valor numérico o no.
     * @return Entidad que representa un objeto columna-valor para generar las sentencias SQL.
    */
    public static DBData createDBData(String columnName, String value,boolean isNumeric) {
        DBDataListCreator dbDataListCreator = new DBSQLHelpper().new DBDataListCreator();
        return dbDataListCreator.addDBData(columnName,value,isNumeric);
    }
}
