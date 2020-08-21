package bit.minisys.minicc.scanner;

import java.util.Formatter;

public class Token {
   public enum T {IF, ID, INT, FLOAT, STR, EOF}

   public T type;
   public Object val;
   public int line;
   public int col;
   public int len;

   public Token(T type, int line, int col) {
      this.type = type;
      this.line = line;
      this.col = col;
   }

   public Token(T type, Object val, int line, int col, int len) {
      this.type = type;
      this.val = val;
      this.line = line;
      this.col = col;
      this.len = len;
   }

   public String toString() {
      Formatter out = new Formatter();
      out.format("(%d,%d) %s", line, col, type);
      if (val != null)
         out.format(" [%s]", val);
      out.format(" (%d-%d)", col, col+len-1);
      return out.toString();
   }
}
