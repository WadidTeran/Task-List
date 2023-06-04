package repositories;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TaskRepository extends DataBaseRepositoryImpl implements IRepository{
    @Override
    public ArrayList findAll() {
        return null;
    }

    @Override
    public Object getById(Long id) {
        return null;
    }

    @Override
    public Object save(Object o) {
        return null;
    }

    @Override
    public void delete(Object o) {

    }
}
