package ordenese.vendor.common.instant_transfer;

/**
 * Created by user on 12/18/2018.
 * Option value transfer
 */

public interface OptionValueTransfer {
    void transferDetail(String optionValue, int sortOrder, boolean postType, int optionValueId);
}
