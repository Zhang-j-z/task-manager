package taskapp.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TaskWriteMapper {
    @Update("UPDATE tasks SET completed = TRUE WHERE id = #{id}")
    int markCompleted(@Param("id") int id);
}
