/**
 * Copyright 2011 Adrian Stabiszewski, as@nfctools.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nfctools.ndef.wkt.encoder.handover;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.nfctools.ndef.NdefConstants;
import org.nfctools.ndef.NdefMessageEncoder;
import org.nfctools.ndef.NdefRecord;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.wkt.encoder.RecordEncoder;
import org.nfctools.ndef.wkt.records.handover.HandoverRequestRecord;
import org.nfctools.ndef.wkt.records.handover.HandoverSelectRecord;

/**
 * 
 * @author Thomas Rorvik Skjolberg (skjolber@gmail.com)
 *
 */

public class HandoverSelectRecordEncoder implements RecordEncoder {

	@Override
	public boolean canEncode(Record record) {
		return record instanceof HandoverSelectRecord;
	}

	@Override
	public NdefRecord encodeRecord(Record record, NdefMessageEncoder messageEncoder) {
		
		HandoverSelectRecord handoverSelectRecord = (HandoverSelectRecord)record;
		
		ByteArrayOutputStream payload = new ByteArrayOutputStream();
		
		// major version, minor version
		payload.write((handoverSelectRecord.getMajorVersion() << 4) | handoverSelectRecord.getMinorVersion());

		// implementation note: write alternative carriers and error record together
		if(handoverSelectRecord.hasError() && handoverSelectRecord.hasAlternativeCarriers()) {
			
			List<Record> records = new ArrayList<Record>();
			
			// n alternative carrier records
			records.addAll(handoverSelectRecord.getAlternativeCarriers());
			
			// an error message
			records.add(handoverSelectRecord.getError());
			
			messageEncoder.encode(records, payload);
		} else if(handoverSelectRecord.hasAlternativeCarriers()) {
			
			// n alternative carrier records
			messageEncoder.encode(handoverSelectRecord.getAlternativeCarriers(), payload);
		} else if(handoverSelectRecord.hasError()){
			
			// an error message
			messageEncoder.encodeSingle(handoverSelectRecord.getError(), payload);
		} else {
			// do nothing
		}
		
		return new NdefRecord(NdefConstants.TNF_WELL_KNOWN, HandoverSelectRecord.TYPE, record.getId(), payload.toByteArray());
	}

}
