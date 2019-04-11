package com.luanlouis.jdbc.instropect;

import com.alibaba.druid.util.StringUtils;

import java.util.StringJoiner;

/**
 * 功能描述:
 * <p>
 * <p>
 * 相关设计：
 *
 * @author louluan(of2256)
 * @date 2019/04/15
 */
public class TableColumnMetaData implements StringDescriptor {

    private static final String SPACE = " ";
    private String TABLE_CAT;

    private String TABLE_SCHEM;

    private String TABLE_NAME;

    private String COLUMN_NAME;

    private Integer DATA_TYPE;

    private String TYPE_NAME;

    private String COLUMN_SIZE;

    private int DECIMAL_DIGITS;

    private Integer NUM_PREC_RADIX;

    private int NULLABLE;

    private String REMARKS;


    public String getTABLE_CAT() {
        return TABLE_CAT;
    }

    public void setTABLE_CAT(String TABLE_CAT) {
        this.TABLE_CAT = TABLE_CAT;
    }

    public String getTABLE_SCHEM() {
        return TABLE_SCHEM;
    }

    public void setTABLE_SCHEM(String TABLE_SCHEM) {
        this.TABLE_SCHEM = TABLE_SCHEM;
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    public String getCOLUMN_NAME() {
        return COLUMN_NAME;
    }

    public void setCOLUMN_NAME(String COLUMN_NAME) {
        this.COLUMN_NAME = COLUMN_NAME;
    }

    public Integer getDATA_TYPE() {
        return DATA_TYPE;
    }

    public void setDATA_TYPE(Integer DATA_TYPE) {
        this.DATA_TYPE = DATA_TYPE;
    }

    public String getTYPE_NAME() {
        return TYPE_NAME;
    }

    public void setTYPE_NAME(String TYPE_NAME) {
        this.TYPE_NAME = TYPE_NAME;
    }

    public String getCOLUMN_SIZE() {
        return COLUMN_SIZE;
    }

    public void setCOLUMN_SIZE(String COLUMN_SIZE) {
        this.COLUMN_SIZE = COLUMN_SIZE;
    }

    public int getDECIMAL_DIGITS() {
        return DECIMAL_DIGITS;
    }

    public void setDECIMAL_DIGITS(int DECIMAL_DIGITS) {
        this.DECIMAL_DIGITS = DECIMAL_DIGITS;
    }

    public Integer getNUM_PREC_RADIX() {
        return NUM_PREC_RADIX;
    }

    public void setNUM_PREC_RADIX(Integer NUM_PREC_RADIX) {
        this.NUM_PREC_RADIX = NUM_PREC_RADIX;
    }

    public int getNULLABLE() {
        return NULLABLE;
    }

    public void setNULLABLE(int NULLABLE) {
        this.NULLABLE = NULLABLE;
    }

    public String getREMARKS() {
        return REMARKS;
    }

    public void setREMARKS(String REMARKS) {
        this.REMARKS = REMARKS;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TableColumnMetaData.class.getSimpleName() + "[", "]")
                .add("TABLE_CAT='" + TABLE_CAT + "'")
                .add("TABLE_SCHEM='" + TABLE_SCHEM + "'")
                .add("TABLE_NAME='" + TABLE_NAME + "'")
                .add("COLUMN_NAME='" + COLUMN_NAME + "'")
                .add("DATA_TYPE=" + DATA_TYPE)
                .add("TYPE_NAME='" + TYPE_NAME + "'")
                .add("COLUMN_SIZE='" + COLUMN_SIZE + "'")
                .add("DECIMAL_DIGITS='" + DECIMAL_DIGITS + "'")
                .add("NUM_PREC_RADIX='" + NUM_PREC_RADIX + "'")
                .add("NULLABLE=" + NULLABLE)
                .add("REMARKS='" + REMARKS + "'")
                .toString();
    }

    @Override
    public String asString() {
        StringBuilder columnBuilder = new StringBuilder();
        columnBuilder.append(this.getCOLUMN_NAME()).append(SPACE);
        columnBuilder.append(getTYPE_NAME()).append("(").append(getCOLUMN_SIZE());
        if (getDECIMAL_DIGITS() != 0) {
            columnBuilder.append(",").append(getDECIMAL_DIGITS());
        }
        columnBuilder.append(")").append(SPACE);
        if (getNULLABLE() == 0) {
            columnBuilder.append("not").append(SPACE);
        }
        columnBuilder.append("nullable").append(SPACE);
        if (!StringUtils.isEmpty(this.getREMARKS())) {
            columnBuilder.append("comments").append(SPACE).append("'").append(this.getREMARKS()).append("'");
        }
       return columnBuilder.toString();
    }
}
