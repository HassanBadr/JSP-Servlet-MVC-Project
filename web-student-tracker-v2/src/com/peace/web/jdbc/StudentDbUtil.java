package com.peace.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	
	private DataSource dataSource;
	
	public StudentDbUtil(DataSource theDataSource){
		dataSource = theDataSource;
	}
	
	public List<Student> getStudents() throws Exception{
		List<Student> students = new ArrayList<>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		try{
			myConn = dataSource.getConnection();
			
			String sql = "select * from student order by last_name";
			
			myStmt = myConn.createStatement();
			
			myRs = myStmt.executeQuery(sql);
			
			while(myRs.next()){
				
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				Student tempStudent = new Student(id,firstName, lastName, email);
				
				students.add(tempStudent);
			}
			
			
			return students;
		}
		finally {
			close(myConn,myStmt,myRs);
		}
		
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try{
			if(myRs !=null){
				myRs.close();
			}
			
			if(myStmt !=null){
				myStmt.close();
			}
			
			if(myConn !=null){
				myConn.close();
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void addStudent(Student theStudent) throws SQLException {
		
		Connection myConn = null;
		
		PreparedStatement myStmt = null;
		try{
			//get db connection
			myConn = dataSource.getConnection();
			//create sql for insert
			String sql = "insert into student "
					+ "(first_name,last_name,email) "
					+ "values(?,?,?)";
			
			myStmt = myConn.prepareStatement(sql);
			//set the param value for student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			//execute sql insert
			myStmt.execute();
		} 
		finally {
			close(myConn, myStmt, null);
		}
		
		
	}

	public Student getStudent(String theStudentId) throws Exception {
		
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;
		try{
			//convert student id to int
			studentId = Integer.parseInt(theStudentId);
			
			//get connection to database
			myConn = dataSource.getConnection();
			
			//create sql to get selected student
			String sql = "select * from student where id=?";
			
			//create prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setInt(1, studentId);
			//execute statement
			myRs = myStmt.executeQuery();
			
			//retrieve data from result set row
			if(myRs.next()){
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				theStudent = new Student(studentId, firstName, lastName, email);
				
				return theStudent;
			}
			else{
				throw new Exception("Could not find student id: "+studentId);
			}
		}
		finally {
			close(myConn, myStmt, myRs);
		}
		
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try{
			
			//get db connection
			myConn = dataSource.getConnection();
			
			//create SQL update statement
			String sql = "update student "
					+ "set first_name=?, last_name=?, email=? "
					+ "where id=?";
			
			//prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			
			//execute SQL statement
			myStmt.execute();
		
		}
		finally {
			//clean up JDBC object
			close(myConn, myStmt, null);
		}
	}

	public void deleteStudent(String theStudentId) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try{
			//convert student id into int
			int studentId = Integer.parseInt(theStudentId);
			
			//get connection to database
			myConn = dataSource.getConnection();
			
			//create sql to delete student
			String sql = "delete from student where id=?";
			
			//prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setInt(1, studentId);
			
			//execute sql statement
			myStmt.execute();
		}
		finally {
			//clean JDBC code
			close(myConn, myStmt, null);
		}
		
	}

	public List<Student> searchStudents(String theSearchName) throws Exception {
		
		List<Student> students = new ArrayList<>();
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		
		try{
			//get connection to database
			myConn = dataSource.getConnection();
			
			//only search by name if theSearchName is not empty
			
			if(theSearchName !=null && theSearchName.trim().length() > 0){
				//create sql to search for students by name
				String sql = "select * from student where lower(first_name) like ? "
						+ "or lower(last_name) like ?";
				
				//create prepared statement
				myStmt = myConn.prepareStatement(sql);
				
				//set params
				String theSearchNamelike = "%"+theSearchName.toLowerCase()+"%";
				myStmt.setString(1, theSearchNamelike);
				myStmt.setString(2, theSearchNamelike);
				
			}
			else{
				//create sql to get all students
				String sql = "select * from student order by last_name";
				
				//create prepared statement
				myStmt = myConn.prepareStatement(sql);
			}
			
			//excute statement
			myRs = myStmt.executeQuery();
			
			//retrieve data from result set row
			while(myRs.next()){
				
				int id= myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				//create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				
				//add it to list of students
				students.add(tempStudent);
			}
			
			return students;
		}
		finally {
			//clean up JDBC objects 
			close(myConn, myStmt, myRs);
			
		}
	}

}
