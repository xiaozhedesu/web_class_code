package club.xiaozhe.cloudservermanager.service;

import club.xiaozhe.cloudservermanager.entity.Server;
import club.xiaozhe.cloudservermanager.repository.ServerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServerService {

    private final ServerRepository serverRepository;

    public ServerService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public List<Server> listAll() {
        return serverRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<Server> findById(Integer id) {
        return serverRepository.findById(id);
    }

    public Server create(Server server) {
        return serverRepository.save(server);
    }

    public Server update(Server server) {
        return serverRepository.save(server);
    }

    public void delete(Integer id) {
        serverRepository.deleteById(id);
    }
}
