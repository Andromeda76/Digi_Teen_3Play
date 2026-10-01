package digiteen3play.model;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@MappedSuperclass
public abstract class AbstractModel {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

}
