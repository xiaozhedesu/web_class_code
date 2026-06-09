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

    public List<ServerResponse> listServers() {
        return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(ServerResponse::from)
                .toList();
    }

    /**
     * 确认服务器套餐存在性，如果存在就返回套餐
     *
     * @param id 套餐id
     * @return 服务器套餐
     */
    private Server getServer(Integer id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SERVER_NOT_FOUND));
    }

    public ServerResponse create(ServerRequest request) {
        return ServerResponse.from(serverRepository.save(request.toServer()));
    }

    public ServerResponse update(Integer id, ServerRequest request) {
        Server existing = getServer(id);

        if (request.model() != null) existing.setModel(request.model());
        if (request.cpu() != null) existing.setCpu(request.cpu());
        if (request.ram() != null) existing.setRam(request.ram());
        if (request.disk() != null) existing.setDisk(request.disk());
        if (request.pricePerMonth() != null) existing.setPricePerMonth(request.pricePerMonth());
        if (request.isAvailable() != null) existing.setIsAvailable(request.isAvailable());

        return ServerResponse.from(serverRepository.save(existing));
    }

    public void delete(Integer id) {
        // 检查套餐是否存在
        getServer(id);
        serverRepository.deleteById(id);
    }
}
