package org.lsf.dao.impl;

import org.lsf.dao.QuestionDao;
import org.lsf.model.Question;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.sql.DriverManager.getConnection;

public class QuestionDaoImpl implements QuestionDao {

    /*
     * 设置变量
     * JDBC driver name
     * database URL
     */
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://118.31.15.23:3306/DB_Examination?useUnicode=true&characterEncoding=utf8";

    /*
     * 设置用户信息变量
     * USER and PASS
     */
    static final String USER = "lsf";
    static final String PASS = "LSFlsf123";

    public Connection conn = null;
    public PreparedStatement pstmt = null;
    public Statement stmt = null;

    public Question parseResultSet(ResultSet resultSet) throws SQLException {
        String ques_id = resultSet.getString("ques_id");
        String ques_stem = resultSet.getString("ques_stem");
        String ques_A = resultSet.getString("ques_A");
        String ques_B = resultSet.getString("ques_B");
        String ques_C = resultSet.getString("ques_C");
        String ques_D = resultSet.getString("ques_D");
        String ques_Correct = resultSet.getString("ques_Correct");
        Question question = new Question(Integer.parseInt(ques_id), ques_stem, ques_A, ques_B, ques_C, ques_D, ques_Correct);

        /*获取题目中的图片信息 ， 没有则设置为null*/
        try {
            InputStream inputStream = resultSet.getBlob("Picture").getBinaryStream();
            byte[] b = new byte[1024];
            int a;
            ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
            while ((a = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, a);
            }
            byte[] b2 = fileOutputStream.toByteArray();
            question.setPhoto(b2);
        } catch (NullPointerException e) {
            question.setPhoto(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return question;
    }

    public Connection connectionToDB() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        conn = getConnection(DB_URL, USER, PASS);
        return conn;
    }

    @Override
    public List<Question> queryById(int id) {
        return null;
    }

    @Override
    public List<Question> queryAllQues() {
        try {
            conn = connectionToDB();
            String sql = "SELECT * FROM tbl_Question ORDER BY rand()";
            stmt = conn.createStatement();
            stmt.execute(sql);
            ResultSet resultSet = stmt.getResultSet();
            List<Question> list = new ArrayList<>();
            while (resultSet.next()) {
                Question question = parseResultSet(resultSet);
                list.add(question);
            }
            return list;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Question> queryQuesByNum(int number) {
        try {
            conn = connectionToDB();
            String sql = "SELECT * FROM tbl_Question ORDER BY rand() LIMIT ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, number);
            ResultSet resultSet = pstmt.executeQuery();
            List<Question> list = new ArrayList<>();

            while (resultSet.next()) {
                Question question = parseResultSet(resultSet);
                list.add(question);
            }

            return list;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<Question>();

    }

    public int getValidLength(byte[] bytes) {
        int i = 0;
        if (null == bytes || 0 == bytes.length)
            return i;
        for (; i < bytes.length; i++) {
            if (bytes[i] == '\0')
                break;
        }
        return i + 1;
    }

    @Override
    public void updatePicture() {
        try {
            File file = new File("../resources/冯诺依曼.jpg");
            FileInputStream fi = new FileInputStream(file);

            conn = connectionToDB();
            String sql = "UPDATE tbl_Question SET picture = ? WHERE ques_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setObject(1, fi);
            pstmt.setObject(2, 6);
            /*执行*/
            int f = pstmt.executeUpdate();

            if (f > 0) {
                System.out.println("插入成功");
            } else {
                System.out.println("插入失败");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Question> queryByNum(int number) {

        return null;
    }

    public static void main(String[] args) {
        /*QuestionDao questionDao = new QuestionDaoImpl();
        List<Question> list = questionDao.queryAllQues();
        for (Question question:list){
            System.out.println(question);
        }*/
        QuestionDao questionDao = new QuestionDaoImpl();
//        questionDao.updatePicture();
//        questionDao.queryAllQues();
        questionDao.updatePicture();
    }
}
