package com.example.learnjavaweb.dao.impl;

import com.example.learnjavaweb.dao.GenericDAO;
import com.example.learnjavaweb.mapper.RowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbstractDAO<T> implements GenericDAO<T> {
    public Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/jsp_servlet_learning";
            String user = "root";
            String password = "1234";
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
    }

    @Override
    public List<T> query(String sql, RowMapper<T> rowMapper, Object... parameters) {
        List<T> results = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            setParameter(statement, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                results.add(rowMapper.mapRow(resultSet));
            }
            return results;
        } catch (SQLException e) {
            return null;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                return null;
            }
        }
    }

    @Override
    public void update(String sql, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);
            setParameter(statement, parameters);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Long insert(String sql, Object... parameters) {
        ResultSet resultSet = null;
        Connection connection = null;
        PreparedStatement statement = null;
        Long id = null;
        try {
            connection = getConnection();  //m??? k???t n???i
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);  //truy???n c??u l???nh sql cho ?????i t?????ng prepareStm
            setParameter(statement, parameters);  //set l???n l?????t c??c tham s???
            statement.executeUpdate();  //th???c thi c??u l???nh sql
            resultSet = statement.getGeneratedKeys();  //tr??? v??? c??i g?? nh???n c??i ?????y
            if (resultSet.next()) {
                id = resultSet.getLong(1);  //tr??? v??? id b??i vi???t (id t??? t??ng, s??? 1 c?? ngh??a l?? tr??? v??? ????ng 1 column - m???c ?????nh lu??n ????? l?? 1)
            }
            //n???u t???t c??? thao t??c trong h??m setParameter() m?? success h???t th?? n?? s??? commit
            connection.commit();  //sau khi commit th?? database m???i changed
            return id;
        } catch (SQLException e) {
            if (connection != null) {  //n???u 1 trong s??? c??c thao t??c trong h??m setParameter() m?? fail
                try {
                    connection.rollback(); //th?? n?? s??? rollback (t???c l?? thu h???i l???i request), n?? s??? reset h???t nh???ng c??i ???? success v??? tr???ng th??i ban ?????u
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                return null;
            }
        }
        return null;
    }

    private void setParameter(PreparedStatement statement, Object... parameters) {
        try {
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                int index = i + 1;
                if (parameter instanceof Long) {
                    statement.setLong(index, (long) parameter);
                } else if (parameter instanceof String) {
                    statement.setString(index, (String) parameter);
                } else if (parameter instanceof Integer) {
                    statement.setInt(index, (Integer) parameter);
                } else if (parameter instanceof Timestamp) {
                    statement.setTimestamp(index, (Timestamp) parameter);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
