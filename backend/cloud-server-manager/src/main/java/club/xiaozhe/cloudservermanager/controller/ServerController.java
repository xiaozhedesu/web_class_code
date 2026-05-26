package club.xiaozhe.cloudservermanager.controller;

import club.xiaozhe.cloudservermanager.dto.ServerResponse;
import club.xiaozhe.cloudservermanager.entity.Server;
import club.xiaozhe.cloudservermanager.service.ServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServerController {

    private final ServerService serverService;

    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * 查询所有服务器套餐（所有登录用户可用）
     * GET /api/servers
     */
    @GetMapping("/servers")
    public ResponseEntity<?> listServers() {
        var servers = serverService.listAll().stream().map(ServerResponse::from).toList();
        return ResponseEntity.ok(servers);
    }

    /**
     * 新增服务器套餐（管理员）
     * POST /api/admin/servers
     */
    @PostMapping("/admin/servers")
    public ResponseEntity<?> createServer(@RequestBody Server server) {
        server.setId(null);
        Server saved = serverService.create(server);
        return ResponseEntity.ok(ServerResponse.from(saved));
    }

    /**
     * 修改服务器套餐（管理员）
     * PUT /api/admin/servers/{id}
     */
    @PutMapping("/admin/servers/{id}")
    public ResponseEntity<?> updateServer(@PathVariable Integer id, @RequestBody Server server) {
        Server existing = serverService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(404).body(Map.of("message", "服务器套餐不存在"));
        }
        if (server.getModel() == null && server.getCpu() == null && server.getRam() == null
                && server.getDisk() == null && server.getPricePerMonth() == null && server.getIsAvailable() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "需要传入修改项"));
        }
        if (server.getModel() != null) existing.setModel(server.getModel());
        if (server.getCpu() != null) existing.setCpu(server.getCpu());
        if (server.getRam() != null) existing.setRam(server.getRam());
        if (server.getDisk() != null) existing.setDisk(server.getDisk());
        if (server.getPricePerMonth() != null) existing.setPricePerMonth(server.getPricePerMonth());
        if (server.getIsAvailable() != null) existing.setIsAvailable(server.getIsAvailable());
        serverService.update(existing);
        return ResponseEntity.ok(ServerResponse.from(existing));
    }

    /**
     * 删除服务器套餐（管理员）
     * DELETE /api/admin/servers/{id}
     */
    @DeleteMapping("/admin/servers/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable Integer id) {
        if (serverService.findById(id).isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "服务器套餐不存在"));
        }
        serverService.delete(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
