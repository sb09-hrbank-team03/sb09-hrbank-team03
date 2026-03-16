package com.sb09.hrbank.repository;

import com.sb09.hrbank.dto.request.ChangeLogListRequest;
import com.sb09.hrbank.entity.ChangeLog;
import com.sb09.hrbank.entity.ChangeType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

// changeLog레포 쿼리 자꾸 터져서 따로 설정
// 이 specification이 뭐냐면 쉽게 말해 where 절 하나하나를 메서드로 만들어두고 조합하는 거라고 보면 됩니다.
// 보시면 (root, query, cb) 라고 뭔가 있는데 이게 조건입니다.
// root는 엔티티 테이블, 그러니까 지금 경우에는 ChangeLog 엔티티
// query는 쿼리 자체인데 딱히 여기서 안 쓰이니 무시하셔도 됩니다
// cb는 이제 여러 조건이 미리 만들어져서 담겨있는 애라고 보시면 됩니다.
// cb.like(), cb.equal(), cb.lessThan() 이런 식으로 가져다 쓰면 됩니다.
public class ChangeLogSpecification {
  // where 절 역할을 해주는 메서드들을 이용해서 하나로 합쳐주는 부분입니다.
  // Specification에 사용 대상 엔티티가 ChangeLog이므로 설정해주고 각 메소드들을 호출하고
  // and로 계속해서 호출해주면 조건이 적용되고 또 적용되고 하는 방식으로 쭉쭉 적용되어서
  // 전부 적용된 값만 return이 되는 형태입니다.
  // 이렇게 해서 return 되는 spec이 바로 저희가 설정한 조건들이 전부 잘 적용이 되어 조건 덩어리입니다.
  // 얘도 pageable처럼 간단하게 repository에 던져주면 됩니다.
  public static Specification<ChangeLog> build(ChangeLogListRequest request) {
    Specification<ChangeLog> spec = likeIfPresent("employeeNumber", request.employeeNumber());
    spec = spec.and(likeIfPresent("memo", request.memo()));
    spec = spec.and(likeIfPresent("ipAddress", request.ipAddress()));
    spec = spec.and(typeEquals(request.type()));
    spec = spec.and(greaterThanOrEqual(request.atFrom()));
    spec = spec.and(lessThanOrEqual(request.atTo()));
    spec = spec.and(idAfter(request.idAfter()));
    return spec;
  }
  // field는 그냥 필드명입니다. 그리고 value는 그 필드에 들어가 있는 값.
  // field라는 필드에 들어온 값, 그니까 value 가 null 이거나 텅 비었다면 null로 처리.
  // 아니라면 root, 그니까 ChangeLog에서 필드 명에 해당되는 필드 가져와서 값이랑 like로 비교해보는 함수입니다.
  // like로 비교해서 일부 일치하는 데이터가 있다면 반환하고,
  // null이 반환된 경우에는 jpa가 조건이 없는 거로 인식해서 자동으로 무시됩니다.
  private static Specification<ChangeLog> likeIfPresent(String field, String value) {
    return (root, query, cb) ->
        (value == null || value.isEmpty()) ? null : cb.like(root.get(field), "%" + value + "%");
  }

  // 아래들도 다 비슷한 원리..
  private static Specification<ChangeLog> typeEquals(String type) {
    return (root, query, cb) ->
        (type == null || type.isEmpty()) ? null : cb.equal(root.get("type"), ChangeType.valueOf(type));
  }

  private static Specification<ChangeLog> greaterThanOrEqual(Instant atFrom) {
    return (root, query, cb) ->
        atFrom == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), atFrom);
  }

  private static Specification<ChangeLog> lessThanOrEqual(Instant atTo) {
    return (root, query, cb) ->
        atTo == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), atTo);
  }

  private static Specification<ChangeLog> idAfter(Long idAfter) {
    return (root, query, cb) ->
        idAfter == null ? null : cb.lessThan(root.get("id"), idAfter);
  }

}
