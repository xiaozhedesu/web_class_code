package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.dto.ServerRequest;
import club.xiaozhe.cloudservermanager.dto.ServerResponse;
import club.xiaozhe.cloudservermanager.entity.Server;
import club.xiaozhe.cloudservermanager.exception.BusinessException;
import club.xiaozhe.cloudservermanager.exception.ErrorCode;
import club.xiaozhe.cloudservermanager.repository.ServerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    /* ----- tools ----- */

    /**
     * 根据id获取服务器套餐信息
     *
     * @param id 服务器套餐id
     * @return 服务器套餐实例对象
     */
    Server findServerById(Integer id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVER_NOT_FOUND));
    }

    /* ----- apis ----- */

    /**
     * 查询所有服务器套餐（所有登录用户可用）
     */
    public List<ServerResponse> listServers() {
        return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(ServerResponse::from)
                .toList();
    }

    /**
     * 新增服务器套餐（管理员）
     */
    public ServerResponse createServer(ServerRequest request) {
        return ServerResponse.from(serverRepository.save(request.toServer()));
    }

    /**
     * 修改服务器套餐（管理员）
     */
    public ServerResponse updateServer(Integer id, ServerRequest request) {
        Server existing = findServerById(id);

        if (request.model() != null) existing.setModel(request.model());
        if (request.cpu() != null) existing.setCpu(request.cpu());
        if (request.ram() != null) existing.setRam(request.ram());
        if (request.disk() != null) existing.setDisk(request.disk());
        if (request.pricePerMonth() != null) existing.setPricePerMonth(request.pricePerMonth());
        if (request.isAvailable() != null) existing.setIsAvailable(request.isAvailable());

        return ServerResponse.from(serverRepository.save(existing));
    }

    /**
     * 删除服务器套餐（管理员）
     */
    public void deleteServer(Integer id) {
        // 检查套餐是否存在
        findServerById(id);
        serverRepository.deleteById(id);
    }
}
