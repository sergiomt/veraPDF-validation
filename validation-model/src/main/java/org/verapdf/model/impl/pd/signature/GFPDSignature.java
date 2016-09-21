package org.verapdf.model.impl.pd.signature;

import org.apache.log4j.Logger;
import org.verapdf.cos.*;
import org.verapdf.model.baselayer.Object;
import org.verapdf.model.external.PKCSDataObject;
import org.verapdf.model.impl.external.GFPKCSDataObject;
import org.verapdf.model.impl.pd.GFPDObject;
import org.verapdf.model.pdlayer.PDSigRef;
import org.verapdf.model.pdlayer.PDSignature;
import org.verapdf.parser.SignatureParser;
import org.verapdf.pd.PDDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergey Shemyakov
 */
public class GFPDSignature extends GFPDObject implements PDSignature {

    private static final Logger LOGGER = Logger.getLogger(GFPDSignature.class);

    /**
     * Type name for {@code PBoxPDSignature}
     */
    public static final String SIGNATURE_TYPE = "PDSignature";

    public static final String CONTENTS = "Contents";
    public static final String REFERENCE = "Reference";

    protected static COSString contents;
    protected long signatureOffset = -1;

    public GFPDSignature(org.verapdf.pd.PDSignature pdSignature,
                         PDDocument document, COSObject signatureReference) {
        super(pdSignature, SIGNATURE_TYPE);
        this.document = document;
        if(signatureReference.isIndirect()) {
            COSKey key = signatureReference.getObjectKey();
            this.signatureOffset = this.document.getDocument().getOffset(key);
        }
        contents = pdSignature.getContents();
    }

    @Override
    public List<? extends Object> getLinkedObjects(String link) {
        switch (link) {
            case CONTENTS:
                return getContents();
            case REFERENCE:
                return getSigRefs();
            default:
                return super.getLinkedObjects(link);
        }
    }

    /**
     * @return DER-encoded PKCS#7 data object representing PDF Signature.
     */
    private List<PKCSDataObject> getContents() {
        if (contents != null) {
            List<PKCSDataObject> list = new ArrayList<>(MAX_NUMBER_OF_ELEMENTS);
            list.add(new GFPKCSDataObject(contents));
            return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    /**
     * @return signature reference dictionaries.
     */
    private List<PDSigRef> getSigRefs() {
        COSArray reference = ((org.verapdf.pd.PDSignature)
                this.simplePDObject).getReference();
        if (reference == null || reference.size() == 0) {
            return Collections.emptyList();
        }
        List<PDSigRef> list = new ArrayList<>();
        for (COSObject sigRef : reference) {
            list.add(new GFPDSigRef((COSDictionary) sigRef.get()));
        }
        return Collections.unmodifiableList(list);
    }


    /**
     * @return true if byte range covers entire document except for Contents
     * entry in signature dictionary
     */
    @Override
    public Boolean getdoesByteRangeCoverEntireDocument() {
        try {
            SignatureParser parser = new SignatureParser(this.document.getPDFSource(),
                    this.document.getDocument());
            long[] actualByteRange =
                    parser.getByteRangeBySignatureOffset(signatureOffset);
            int[] byteRange = ((org.verapdf.pd.PDSignature) this.simplePDObject).getByteRange();
            for (int i = 0; i < 3; ++i) {
                if (byteRange[i] != actualByteRange[i]) {
                    return false;
                }
            }
            return true;
        } catch (IOException ex) {
            LOGGER.debug("Can't create parser to process digital signature", ex);
            return false;
        }
    }
}