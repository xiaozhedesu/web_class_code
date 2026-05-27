package club.xiaozhe.cloudservermanager.exception;

public class UserNotFoundException extends BusinessException{
    public UserNotFoundException() {
        super("用户不存在！");
    }

    public UserNotFoundException(String name) {
        super(String.format("用户 %s 不存在！", name));
    }
}
