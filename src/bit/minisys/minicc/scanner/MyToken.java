package bit.minisys.minicc.scanner;

import java.util.Formatter;

public class MyToken {
   public enum T {
    Constant, Identifier, StringLiteral,
    KEYWORD, SIGN, OPERATOR,
    EOF}

   public T type;
   public Object val;
   public int line;
   public int col;
   public int len;
   public int index;
   public int num;

   public MyToken(T type, Object val, int line, int col, int len, int index, int num) {
      this.type = type;
      this.val = val;
      this.line = line;
      this.col = col;
      this.len = len;
      this.index = index;
      this.num = num;
   }

   public String toString() {
      Formatter out = new Formatter();
      out.format("[@%d,%d:%d", num, index, index+len-1);
      out.format("='%s',", val);
      if (type == T.KEYWORD || type == T.SIGN || type == T.OPERATOR)
        out.format("<'%s'>,", val);
      else
        out.format("<%s>,", type);
      out.format("%d:%d]", line+1, col);
      return out.toString();
   }
}