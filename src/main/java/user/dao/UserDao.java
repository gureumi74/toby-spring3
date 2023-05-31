package user.dao;

import user.domain.User;

import java.sql.*;
import java.util.Map;

import static java.lang.System.getenv;

public abstract class UserDao {
    // 예외는 모두 메소드 밖으로 던지는게 편해서 지금은 따로 처리 안함
    // 유저 정보 추가
    public void add(User user) throws ClassNotFoundException, SQLException {
        Connection conn = getConnection();
        // SQL을 담은 Statenment를 만든다.
        PreparedStatement ps = conn.prepareStatement(
                // 만들어진 Statement를 실행한다.
                "insert into users(id, name, password) values (?, ?, ?)");
        ps.setString(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getPassword());

        ps.executeUpdate();

        // 작업 중에 생성된 Connection, Statement 등과 같은 리소스는 작업을 마친 후 반드시 닫아준다.
        ps.close();
        conn.close();
    }

    // 유저 정보 조회
    public User get(String id) throws ClassNotFoundException, SQLException {
        Connection conn = getConnection();

        // SQL을 담은 Statenment를 만든다.
        PreparedStatement ps = conn.prepareStatement(
                // 만들어진 Statement를 실행한다.
                "select * from users where id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        rs.next();
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPassword(rs.getString("password"));

        // 작업 중에 생성된 Connection, Statement 등과 같은 리소스는 작업을 마친 후 반드시 닫아준다.
        rs.close();
        ps.close();
        conn.close();

        return user;
    }

    public abstract Connection getConnection() throws SQLException;
//    {
//        //        Class.forName("jdbc:sqlite:toby-spring.db");
//        // DB 연결을 위한 Connection을 가져온다.
//        Connection conn = DriverManager.getConnection(
//                "jdbc:sqlite:toby-spring.db");
//
//        return conn;
//    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        UserDao dao = new UserDao() {
            Map<String, String> env = getenv();
            String dbHost = env.get("DB_HOST");

            // 각자가 원하는 방식으로 DB 연결 구현 가능
            @Override
            public Connection getConnection() throws SQLException {
                Connection conn = DriverManager.getConnection(
                        dbHost);

                return conn;
            }
        };

        User user = new User();
        user.setId("1");
        user.setName("김하늘");
        user.setPassword("1234");

//        dao.add(user);
//        System.out.println(user.getId() + " 등록 성공");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());
    }
}
