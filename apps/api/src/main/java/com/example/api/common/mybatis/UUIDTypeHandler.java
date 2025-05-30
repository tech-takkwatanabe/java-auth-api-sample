package com.example.api.common.mybatis;

import com.example.api.auth.domain.vo.UUID;
import java.sql.*;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setObject(i, parameter.getValue());
  }

  @Override
  public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object value = rs.getObject(columnName);
    return value != null ? UUID.from((String) value) : null;
  }

  @Override
  public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    Object value = rs.getObject(columnIndex);
    return value != null ? UUID.from((String) value) : null;
  }

  @Override
  public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    Object value = cs.getObject(columnIndex);
    return value != null ? UUID.from((String) value) : null;
  }
}
