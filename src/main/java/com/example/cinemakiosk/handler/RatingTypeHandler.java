package com.example.cinemakiosk.handler;

import com.example.cinemakiosk.domain.enums.Rating;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Rating.class)
public class RatingTypeHandler extends BaseTypeHandler<Rating> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Rating parameter, JdbcType jdbcType) throws SQLException {
        // Enum -> DB (저장 시 "12", "15" 등으로 저장)
        ps.setString(i, parameter.getConversion());
    }

    @Override
    public Rating getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // DB -> Enum (컬럼명으로 조회)
        return Rating.fromConversion(rs.getString(columnName));
    }

    @Override
    public Rating getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // DB -> Enum (인덱스로 조회)
        return Rating.fromConversion(rs.getString(columnIndex));
    }

    @Override
    public Rating getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // DB -> Enum (프로시저 등)
        return Rating.fromConversion(cs.getString(columnIndex));
    }
}