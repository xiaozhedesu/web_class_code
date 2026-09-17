package club.xiaozhe.shinycloud.controller;

import club.xiaozhe.shinycloud.dto.Result;
import club.xiaozhe.shinycloud.dto.ServerRequest;
import club.xiaozhe.shinycloud.dto.ServerResponse;
import club.xiaozhe.shinycloud.service.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ServerController {

    private final ServerService serverService;

    /**
     * 查询所有服务器套餐（所有登录用户可用）
     *
     * @apiNote GET /api/servers
     */
    @GetMapping("/servers")
    public Result<List<ServerResponse>> listServers() {
        return Result.success(serverService.listServers());
    }

    /**
     * 新增服务器套餐（管理员）
     *
     * @apiNote POST /api/admin/servers
     */
    @PostMapping("/admin/servers")
    public Result<ServerResponse> createServer(@RequestBody @Valid ServerRequest request) {
        return Result.success(serverService.createServer(request));
    }

    /**
     * 修改服务器套餐（管理员）
     *
     * @apiNote PUT /api/admin/servers/{id}
     */
    @PutMapping("/admin/servers/{id}")
    public Result<ServerResponse> updateServer(@PathVariable Integer id, @RequestBody @Valid ServerRequest request) {
        return Result.success(serverService.updateServer(id, request));
    }

    /**
     * 删除服务器套餐（管理员）
     *
     * @apiNote DELETE /api/admin/servers/{id}
     */
    @DeleteMapping("/admin/servers/{id}")
    public Result<Void> deleteServer(@PathVariable Integer id) {
        serverService.deleteServer(id);
        return Result.success(null);
    }
}
