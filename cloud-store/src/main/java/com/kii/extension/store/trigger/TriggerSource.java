package com.kii.extension.store.trigger;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class TriggerSource {



	private TagSelector selector;

	@JsonUnwrapped
	public TagSelector getSelector() {
		return selector;
	}

	public void setSelector(TagSelector selector) {
		this.selector = selector;
	}
}
