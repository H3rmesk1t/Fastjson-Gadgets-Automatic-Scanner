package org.apache.commons.configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.sql.DataSource;
import org.apache.commons.logging.LogFactory;

public class DatabaseConfiguration extends AbstractConfiguration {
   private final DataSource datasource;
   private final String table;
   private final String nameColumn;
   private final String keyColumn;
   private final String valueColumn;
   private final String name;
   private final boolean doCommits;

   public DatabaseConfiguration(DataSource datasource, String table, String nameColumn, String keyColumn, String valueColumn, String name) {
      this(datasource, table, nameColumn, keyColumn, valueColumn, name, false);
   }

   public DatabaseConfiguration(DataSource datasource, String table, String nameColumn, String keyColumn, String valueColumn, String name, boolean commits) {
      this.datasource = datasource;
      this.table = table;
      this.nameColumn = nameColumn;
      this.keyColumn = keyColumn;
      this.valueColumn = valueColumn;
      this.name = name;
      this.doCommits = commits;
      this.setLogger(LogFactory.getLog(this.getClass()));
      this.addErrorLogListener();
   }

   public DatabaseConfiguration(DataSource datasource, String table, String keyColumn, String valueColumn) {
      this(datasource, table, (String)null, keyColumn, valueColumn, (String)null);
   }

   public DatabaseConfiguration(DataSource datasource, String table, String keyColumn, String valueColumn, boolean commits) {
      this(datasource, table, (String)null, keyColumn, valueColumn, (String)null, commits);
   }

   public boolean isDoCommits() {
      return this.doCommits;
   }

   public Object getProperty(String key) {
      Object result = null;
      StringBuilder query = new StringBuilder("SELECT * FROM ");
      query.append(this.table).append(" WHERE ");
      query.append(this.keyColumn).append("=?");
      if (this.nameColumn != null) {
         query.append(" AND " + this.nameColumn + "=?");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         pstmt.setString(1, key);
         if (this.nameColumn != null) {
            pstmt.setString(2, this.name);
         }

         rs = pstmt.executeQuery();
         ArrayList results = new ArrayList();

         while(true) {
            while(rs.next()) {
               Object value = rs.getObject(this.valueColumn);
               if (this.isDelimiterParsingDisabled()) {
                  results.add(value);
               } else {
                  Iterator it = PropertyConverter.toIterator(value, this.getListDelimiter());

                  while(it.hasNext()) {
                     results.add(it.next());
                  }
               }
            }

            if (!results.isEmpty()) {
               result = results.size() > 1 ? results : results.get(0);
            }
            break;
         }
      } catch (SQLException var13) {
         this.fireError(5, key, (Object)null, var13);
      } finally {
         this.close(conn, pstmt, rs);
      }

      return result;
   }

   protected void addPropertyDirect(String key, Object obj) {
      StringBuilder query = new StringBuilder("INSERT INTO " + this.table);
      if (this.nameColumn != null) {
         query.append(" (" + this.nameColumn + ", " + this.keyColumn + ", " + this.valueColumn + ") VALUES (?, ?, ?)");
      } else {
         query.append(" (" + this.keyColumn + ", " + this.valueColumn + ") VALUES (?, ?)");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         int index = 1;
         if (this.nameColumn != null) {
            pstmt.setString(index++, this.name);
         }

         pstmt.setString(index++, key);
         pstmt.setString(index++, String.valueOf(obj));
         pstmt.executeUpdate();
         this.commitIfRequired(conn);
      } catch (SQLException var10) {
         this.fireError(1, key, obj, var10);
      } finally {
         this.close(conn, pstmt, (ResultSet)null);
      }

   }

   public void addProperty(String key, Object value) {
      boolean parsingFlag = this.isDelimiterParsingDisabled();

      try {
         if (value instanceof String) {
            this.setDelimiterParsingDisabled(true);
         }

         super.addProperty(key, value);
      } finally {
         this.setDelimiterParsingDisabled(parsingFlag);
      }

   }

   public boolean isEmpty() {
      boolean empty = true;
      StringBuilder query = new StringBuilder("SELECT count(*) FROM " + this.table);
      if (this.nameColumn != null) {
         query.append(" WHERE " + this.nameColumn + "=?");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         if (this.nameColumn != null) {
            pstmt.setString(1, this.name);
         }

         rs = pstmt.executeQuery();
         if (rs.next()) {
            empty = rs.getInt(1) == 0;
         }
      } catch (SQLException var10) {
         this.fireError(5, (String)null, (Object)null, var10);
      } finally {
         this.close(conn, pstmt, rs);
      }

      return empty;
   }

   public boolean containsKey(String key) {
      boolean found = false;
      StringBuilder query = new StringBuilder("SELECT * FROM " + this.table + " WHERE " + this.keyColumn + "=?");
      if (this.nameColumn != null) {
         query.append(" AND " + this.nameColumn + "=?");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         pstmt.setString(1, key);
         if (this.nameColumn != null) {
            pstmt.setString(2, this.name);
         }

         rs = pstmt.executeQuery();
         found = rs.next();
      } catch (SQLException var11) {
         this.fireError(5, key, (Object)null, var11);
      } finally {
         this.close(conn, pstmt, rs);
      }

      return found;
   }

   protected void clearPropertyDirect(String key) {
      StringBuilder query = new StringBuilder("DELETE FROM " + this.table + " WHERE " + this.keyColumn + "=?");
      if (this.nameColumn != null) {
         query.append(" AND " + this.nameColumn + "=?");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         pstmt.setString(1, key);
         if (this.nameColumn != null) {
            pstmt.setString(2, this.name);
         }

         pstmt.executeUpdate();
         this.commitIfRequired(conn);
      } catch (SQLException var9) {
         this.fireError(2, key, (Object)null, var9);
      } finally {
         this.close(conn, pstmt, (ResultSet)null);
      }

   }

   public void clear() {
      this.fireEvent(4, (String)null, (Object)null, true);
      StringBuilder query = new StringBuilder("DELETE FROM " + this.table);
      if (this.nameColumn != null) {
         query.append(" WHERE " + this.nameColumn + "=?");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         if (this.nameColumn != null) {
            pstmt.setString(1, this.name);
         }

         pstmt.executeUpdate();
         this.commitIfRequired(conn);
      } catch (SQLException var8) {
         this.fireError(4, (String)null, (Object)null, var8);
      } finally {
         this.close(conn, pstmt, (ResultSet)null);
      }

      this.fireEvent(4, (String)null, (Object)null, false);
   }

   public Iterator getKeys() {
      Collection keys = new ArrayList();
      StringBuilder query = new StringBuilder("SELECT DISTINCT " + this.keyColumn + " FROM " + this.table);
      if (this.nameColumn != null) {
         query.append(" WHERE " + this.nameColumn + "=?");
      }

      Connection conn = null;
      PreparedStatement pstmt = null;
      ResultSet rs = null;

      try {
         conn = this.getConnection();
         pstmt = conn.prepareStatement(query.toString());
         if (this.nameColumn != null) {
            pstmt.setString(1, this.name);
         }

         rs = pstmt.executeQuery();

         while(rs.next()) {
            keys.add(rs.getString(1));
         }
      } catch (SQLException var10) {
         this.fireError(5, (String)null, (Object)null, var10);
      } finally {
         this.close(conn, pstmt, rs);
      }

      return keys.iterator();
   }

   public DataSource getDatasource() {
      return this.datasource;
   }

   /** @deprecated */
   @Deprecated
   protected Connection getConnection() throws SQLException {
      return this.getDatasource().getConnection();
   }

   private void close(Connection conn, Statement stmt, ResultSet rs) {
      try {
         if (rs != null) {
            rs.close();
         }
      } catch (SQLException var7) {
         this.getLogger().error("An error occurred on closing the result set", var7);
      }

      try {
         if (stmt != null) {
            stmt.close();
         }
      } catch (SQLException var6) {
         this.getLogger().error("An error occured on closing the statement", var6);
      }

      try {
         if (conn != null) {
            conn.close();
         }
      } catch (SQLException var5) {
         this.getLogger().error("An error occured on closing the connection", var5);
      }

   }

   private void commitIfRequired(Connection conn) throws SQLException {
      if (this.isDoCommits()) {
         conn.commit();
      }

   }
}
