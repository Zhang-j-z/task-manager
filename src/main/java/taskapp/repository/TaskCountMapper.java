package taskapp.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TaskCountMapper {
    /*
    @Select("SELECT COUNT(*) FROM tasks")
    long countAll();

    @Select("SELECT COUNT(*) FROM tasks WHERE completed = #{completed}")
    long countByCompleted(@Param("completed") boolean completed);
     */

    @Select("""
    <script>
    SELECT COUNT(*) FROM tasks
    <where>
        <if test="completed != null">
            completed = #{completed}
        </if>
    </where>
    </script>
    """)
    long count(@Param("completed") Boolean completed);
}
