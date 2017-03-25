package jodd.db.oom;

import jodd.db.oom.meta.DbColumn;
import jodd.db.oom.meta.DbId;
import jodd.db.oom.meta.DbTable;

@DbTable("tester")
public class Tester3 {

	enum Status {
		NEW, PARTIAL, FAILED, SELECTED, UPLOADED
	}

	@DbId
	protected Long id;

	@DbColumn
	protected  Status name;

	@DbColumn
	protected Integer value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Status getName() {
		return name;
	}

	public void setName(Status name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
}
