package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.ApiResponse;
import club.xiaozhe.cloudservermanager.dto.ServerRequest;
import club.xiaozhe.cloudservermanager.dto.ServerResponse;
import club.xiaozhe.cloudservermanager.service.ServerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServerController {

    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * 查询所有服务器套餐（所有登录用户可用）
     *
     * @apiNote GET /api/servers
     */
    @GetMapping("/servers")
    public ApiResponse<List<ServerResponse>> listServers() {
        return ApiResponse.success(serverService.listServers());
    }

    /**
     * 新增服务器套餐（管理员）
     *
     * @apiNote POST /api/admin/servers
     */
    @PostMapping("/admin/servers")
    public ApiResponse<ServerResponse> createServer(@RequestBody @Valid ServerRequest request) {
        return ApiResponse.success(serverService.createServer(request));
    }

    /**
     * 修改服务器套餐（管理员）
     *
     * @apiNote PUT /api/admin/servers/{id}
     */
    @PutMapping("/admin/servers/{id}")
    public ApiResponse<ServerResponse> updateServer(@PathVariable Integer id, @RequestBody @Valid ServerRequest request) {
        return ApiResponse.success(serverService.updateServer(id, request));
    }

    /**
     * 删除服务器套餐（管理员）
     *
     * @apiNote DELETE /api/admin/servers/{id}
     */
    @DeleteMapping("/admin/servers/{id}")
    public ApiResponse<Void> deleteServer(@PathVariable Integer id) {
        serverService.deleteServer(id);
        return ApiResponse.success(null);
    }
}
