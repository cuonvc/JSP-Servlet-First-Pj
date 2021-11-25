package com.example.learnjavaweb.dao;

import com.example.learnjavaweb.mapper.RowMapper;

import java.util.List;

public interface GenericDAO<T> {
    <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... parameters);

}