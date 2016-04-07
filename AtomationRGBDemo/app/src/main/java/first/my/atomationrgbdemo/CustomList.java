package first.my.atomationrgbdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String>
{

	private final Activity	context;

	List<String>	nameArr	= new ArrayList<String>();
	List<String>	addressArr	= new ArrayList<String>();

	public CustomList(Activity context, List<String> nameArr, List<String> addressArr)
	{
		super(context, R.layout.list_single, nameArr);
		this.context = context;
		this.nameArr = nameArr;
		this.addressArr = addressArr;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);
		TextView textName = (TextView) rowView.findViewById(R.id.textName);
		String name = nameArr.get(position);
		if (name == null)
			name = "Unknown";
		textName.setText(name);

		TextView textAddress = (TextView) rowView.findViewById(R.id.textAddress);
		textAddress.setText(addressArr.get(position));
		return rowView;
	}
}