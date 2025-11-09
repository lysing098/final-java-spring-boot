package com.example.finaljava.service;

import com.example.finaljava.exceptions.MyResourceNotFoundException;
import com.example.finaljava.model.Ratting;
import com.example.finaljava.repository.RattingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RattingService {

    private final RattingRepository rattingRepository;

    public List<Ratting> getAllRattings() {
        return rattingRepository.findAll();
    }

    public Ratting getRattingById(int id) {
        return rattingRepository.findById(id)
                .orElseThrow(() -> new MyResourceNotFoundException("Ratting not found with id: " + id));
    }

    public Ratting createRatting(Ratting ratting) {
        return rattingRepository.save(ratting);
    }

    public Ratting updateRatting(int id, Ratting rattingDetails) {
        Ratting existing = getRattingById(id);
        existing.setRate(rattingDetails.getRate());
        existing.setCount(rattingDetails.getCount());
        // no product reference
        return rattingRepository.save(existing);
    }

    public void deleteRatting(int id) {
        Ratting ratting = getRattingById(id);
        rattingRepository.delete(ratting);
    }
}
