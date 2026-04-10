package org.example.repository;

public interface InstructionRepository {

    void save(InstructionLog logEntry);

    void update(InstructionLog logEntry);
}
