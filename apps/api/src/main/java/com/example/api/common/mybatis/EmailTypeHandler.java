package com.example.api.common.mybatis;

import com.example.api.auth.domain.vo.Email;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

public class EmailTypeHandler extends BaseTypeHandler<Email> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Email parameter, JdbcType jdbcType) throws SQLException {
    ps.setString(i, parameter.getValue());
  }

  @Override
  public Email getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String value = rs.getString(columnName);
    return value != null ? new Email(value) : null;
  }

  @Override
  public Email getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String value = rs.getString(columnIndex);
    return value != null ? new Email(value) : null;
  }

  @Override
  public Email getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String value = cs.getString(columnIndex);
    return value != null ? new Email(value) : null;
  }
}
