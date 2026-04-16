package org.example.repository.impl;

import org.example.repository.InstructionLog;
import org.example.repository.InstructionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InstructionRepositoryImpl implements InstructionRepository {

    @Override
    public void save(InstructionLog logEntry) {

    }

    @Override
    public void update(InstructionLog logEntry) {

    }

    @Override
    public InstructionLog findByUpstreamRequestNo(String upstreamRequestNo) {
        return null;
    }
}
