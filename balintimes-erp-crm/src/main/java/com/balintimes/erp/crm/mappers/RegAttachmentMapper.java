package com.balintimes.erp.crm.mappers;

import java.util.List;

import com.balintimes.erp.crm.model.RegAttachment;

public interface RegAttachmentMapper {
	List<RegAttachment> getRegAttachmentByCustomer(int customerUid);

	RegAttachment getRegAttachment(int uid);

	void createRegAttachment(RegAttachment regAttachment);

	void deleteRegAttachment(int uid);
}
