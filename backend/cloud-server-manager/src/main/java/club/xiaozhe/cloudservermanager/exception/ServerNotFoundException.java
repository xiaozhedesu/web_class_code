package club.xiaozhe.cloudservermanager.exception;

public class ServerNotFoundException extends RuntimeException {
    public ServerNotFoundException() {
        super("服务器套餐不存在！");
    }
}
