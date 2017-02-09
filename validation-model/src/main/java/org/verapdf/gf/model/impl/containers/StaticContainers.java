/**
 * This file is part of validation-model, a module of the veraPDF project.
 * Copyright (c) 2015, veraPDF Consortium <info@verapdf.org>
 * All rights reserved.
 *
 * validation-model is free software: you can redistribute it and/or modify
 * it under the terms of either:
 *
 * The GNU General public license GPLv3+.
 * You should have received a copy of the GNU General Public License
 * along with validation-model as the LICENSE.GPL file in the root of the source
 * tree.  If not, see http://www.gnu.org/licenses/ or
 * https://www.gnu.org/licenses/gpl-3.0.en.html.
 *
 * The Mozilla Public License MPLv2+.
 * You should have received a copy of the Mozilla Public License along with
 * validation-model as the LICENSE.MPL file in the root of the source tree.
 * If a copy of the MPL was not distributed with this file, you can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
package org.verapdf.gf.model.impl.containers;

import org.verapdf.as.ASAtom;
import org.verapdf.cos.COSKey;
import org.verapdf.gf.model.impl.pd.colors.GFPDSeparation;
import org.verapdf.gf.model.impl.pd.util.TaggedPDFRoleMapHelper;
import org.verapdf.model.pdlayer.PDColorSpace;
import org.verapdf.pd.PDDocument;
import org.verapdf.pdfa.flavours.PDFAFlavour;

import java.util.*;

/**
 * @author Timur Kamalov
 */
public class StaticContainers {

	private static ThreadLocal<PDDocument> document;
	private static ThreadLocal<PDFAFlavour> flavour;

	// TaggedPDF
	public static ThreadLocal<TaggedPDFRoleMapHelper> roleMapHelper;

	//PBoxPDSeparation
	public static ThreadLocal<Map<String, List<GFPDSeparation>>> separations;
	public static ThreadLocal<List<String>> inconsistentSeparations;

	//ColorSpaceFactory
	public static ThreadLocal<Map<String, PDColorSpace>> cachedColorSpaces;

	public static ThreadLocal<Set<COSKey>> fileSpecificationKeys;

	public static void clearAllContainers() {
		document = new ThreadLocal<PDDocument>();
		flavour = new ThreadLocal<PDFAFlavour>();
		roleMapHelper = new ThreadLocal<TaggedPDFRoleMapHelper>();
		separations = new ThreadLocal<Map<String, List<GFPDSeparation>>>();
		separations.set(new HashMap<String,List<GFPDSeparation>>());
		inconsistentSeparations = new ThreadLocal<List<String>>();
		inconsistentSeparations.set(new ArrayList<String>());
		cachedColorSpaces = new ThreadLocal<Map<String, PDColorSpace>>();
		cachedColorSpaces.set(new HashMap<String,PDColorSpace>());
		fileSpecificationKeys = new ThreadLocal<Set<COSKey>>();
		fileSpecificationKeys.set(new HashSet<COSKey>());
	}

	public static PDDocument getDocument() {
		return document.get();
	}

	public static void setDocument(PDDocument document) {
		StaticContainers.document.set(document);
	}

	public static PDFAFlavour getFlavour() {
		return flavour.get();
	}

	public static void setFlavour(PDFAFlavour flavour) {
		StaticContainers.flavour.set(flavour);
		if (roleMapHelper.get() != null) {
			roleMapHelper.get().setFlavour(flavour);
		}
	}

	public static TaggedPDFRoleMapHelper getRoleMapHelper() {
		return roleMapHelper.get();
	}

	public static void setRoleMapHelper(Map<ASAtom, ASAtom> roleMap) {
		StaticContainers.roleMapHelper.set(new TaggedPDFRoleMapHelper(roleMap, StaticContainers.flavour.get()));
	}
}
