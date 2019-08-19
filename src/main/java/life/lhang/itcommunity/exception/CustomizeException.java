package life.lhang.itcommunity.exception;

/**
 * 运行时异常
 * 此类的作用：当service、dao层发生异常时，方便抛向控制层统一处理。
 * 另：spring默认回滚的是RuntimeException,运行时异常或运行时异常的子异常
 */
public class CustomizeException extends RuntimeException {
    private String message;
    private Integer code;

    public CustomizeException(ICustomizeErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }
}
