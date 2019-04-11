package com.luanlouis.jdbc.instropect;

import java.util.StringJoiner;

/**
 * jdbc index metadata
 */
public class IndexMetaData {

    private String TABLE_CATALOG;

    private String TABLE_SCHEMA;

    private String TABLE_NAME;

    private boolean NON_UNIQUE;

    private String INDEX_NAME;

    private String INDEX_TYPE;

    private Integer ORDINAL_POSITION;

    private String COLUMN_NAME;

    private String ASC_OR_DESC;


    public String getTABLE_CATALOG() {
        return TABLE_CATALOG;
    }

    public void setTABLE_CATALOG(String TABLE_CATALOG) {
        this.TABLE_CATALOG = TABLE_CATALOG;
    }

    public String getTABLE_SCHEMA() {
        return TABLE_SCHEMA;
    }

    public void setTABLE_SCHEMA(String TABLE_SCHEMA) {
        this.TABLE_SCHEMA = TABLE_SCHEMA;
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    public boolean isNON_UNIQUE() {
        return NON_UNIQUE;
    }

    public void setNON_UNIQUE(boolean NON_UNIQUE) {
        this.NON_UNIQUE = NON_UNIQUE;
    }

    public String getINDEX_NAME() {
        return INDEX_NAME;
    }

    public void setINDEX_NAME(String INDEX_NAME) {
        this.INDEX_NAME = INDEX_NAME;
    }

    public String getINDEX_TYPE() {
        return INDEX_TYPE;
    }

    public void setINDEX_TYPE(String INDEX_TYPE) {
        this.INDEX_TYPE = INDEX_TYPE;
    }

    public Integer getORDINAL_POSITION() {
        return ORDINAL_POSITION;
    }

    public void setORDINAL_POSITION(Integer ORDINAL_POSITION) {
        this.ORDINAL_POSITION = ORDINAL_POSITION;
    }

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public String getASC_OR_DESC() {
        return ASC_OR_DESC;
    }

    public void setASC_OR_DESC(String ASC_OR_DESC) {
        this.ASC_OR_DESC = ASC_OR_DESC;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IndexMetaData.class.getSimpleName() + "[", "]")
                .add("TABLE_CATALOG='" + TABLE_CATALOG + "'")
                .add("TABLE_SCHEMA='" + TABLE_SCHEMA + "'")
                .add("TABLE_NAME='" + TABLE_NAME + "'")
                .add("NON_UNIQUE=" + NON_UNIQUE)
                .add("INDEX_NAME='" + INDEX_NAME + "'")
                .add("INDEX_TYPE='" + INDEX_TYPE + "'")
                .add("ORDINAL_POSITION=" + ORDINAL_POSITION)
                .add("COLUMN_NAME='" + COLUMN_NAME + "'")
                .add("ASC_OR_DESC='" + ASC_OR_DESC + "'")
                .toString();
    }

}
