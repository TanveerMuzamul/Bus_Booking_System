package com.busbooking.system.service;

import com.busbooking.system.model.Bus;
import com.busbooking.system.repository.BusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusServiceImpl implements BusService {

    private static final Logger logger = LoggerFactory.getLogger(BusServiceImpl.class);

    private final BusRepository busRepository;

    public BusServiceImpl(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public List<Bus> getAllBuses() {
        List<Bus> buses = busRepository.findAll();
        logger.info("Found {} buses", buses.size());
        return buses;
    }

    @Override
    public List<Bus> getActiveBuses() {
        List<Bus> list = busRepository.findAll()
                .stream().filter(Bus::isActive).toList();
        logger.info("Found {} active buses", list.size());
        return list;
    }

    @Override
    public List<Bus> searchBuses(String source, String dest, String date) {
        List<Bus> list = busRepository.findAll().stream()
                .filter(Bus::isActive)
                .filter(b -> source == null || source.isEmpty() || b.getSource().equalsIgnoreCase(source))
                .filter(b -> dest == null || dest.isEmpty() || b.getDestination().equalsIgnoreCase(dest))
                .toList();

        logger.info("Search returned {} buses", list.size());
        return list;
    }

    @Override
    public Bus saveBus(Bus bus) {
        return busRepository.save(bus);
    }

    @Override
    public void deleteBus(Long id) {
        busRepository.deleteById(id);
    }

    @Override
    public Bus getBusById(Long id) {
        return busRepository.findById(id).orElse(null);
    }

    @Override
    public List<String> getAllSources() {
        return busRepository.findAll().stream()
                .map(Bus::getSource).distinct().toList();
    }

    @Override
    public List<String> getAllDestinations() {
        return busRepository.findAll().stream()
                .map(Bus::getDestination).distinct().toList();
    }
}
